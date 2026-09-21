package dynamicUi.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dynamicUi.demo.dto.PageJsonDTO;
import dynamicUi.demo.entity.UIComponentAction;
import dynamicUi.demo.entity.UIPage;
import dynamicUi.demo.entity.UIPageAction;
import dynamicUi.demo.entity.UIPageJson;
import dynamicUi.demo.repoistory.UIComponentActionRepository;
import dynamicUi.demo.repoistory.UIPageActionRepository;
import dynamicUi.demo.repoistory.UIPageJsonRepository;
import dynamicUi.demo.repoistory.UIPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PageConfigurationCacheService {

    private final UIPageJsonRepository uiPageJsonRepository;
    private final UIPageActionRepository uiPageActionRepository;
    private final UIPageRepository uiPageRepository;
    private final UIComponentActionRepository uiComponentActionRepository;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Loads and assembles the page configuration.
     *
     * This method is cached.
     *
     * IMPORTANT:
     * Authorization is NOT performed here.
     * Authorization is performed by PageAssemblerService
     * before this method is called.
     *
     * First request:
     *
     *     Cache MISS
     *          ↓
     *     Database
     *          ↓
     *     Assemble JSON
     *          ↓
     *     Store in cache
     *
     * Next request:
     *
     *     Cache HIT
     *          ↓
     *     Return cached PageJsonDTO
     */
    @Cacheable(
            value = "pageConfigurations",
            key = "#pageCode"
    )
    public PageJsonDTO getPageConfiguration(String pageCode) {

        System.out.println(
                "========== CACHE MISS :: Loading page from DB :: "
                        + pageCode + " =========="
        );

        // ---------------------------------------------------------
        // 1. Get UIPage
        // ---------------------------------------------------------
        UIPage uiPage = uiPageRepository.findByPageCode(pageCode)
                .orElseThrow(() -> new RuntimeException(
                        "PAGE NOT FOUND :: pageCode = " + pageCode
                ));

        // ---------------------------------------------------------
        // 2. Get base JSON schema
        // ---------------------------------------------------------
        UIPageJson uiPageJson =
                uiPageJsonRepository.findByUiPage_PageCode(pageCode)
                        .orElseThrow(() -> new RuntimeException(
                                "PAGE JSON NOT FOUND :: pageCode = "
                                        + pageCode
                        ));

        try {

            // -----------------------------------------------------
            // 3. Parse JSON
            // -----------------------------------------------------
            ObjectNode pageSchemaNode =
                    (ObjectNode) OBJECT_MAPPER.readTree(
                            uiPageJson.getJsonSchema()
                    );

            // -----------------------------------------------------
            // 4. Add latest page information
            // -----------------------------------------------------
            enrichPageNode(pageSchemaNode, uiPage);

            // -----------------------------------------------------
            // 5. Ensure components array exists
            // -----------------------------------------------------
            if (!pageSchemaNode.has("components")
                    || !pageSchemaNode.get("components").isArray()) {

                pageSchemaNode.set(
                        "components",
                        OBJECT_MAPPER.createArrayNode()
                );
            }

            // -----------------------------------------------------
            // 6. Page-level actions
            // -----------------------------------------------------
            //
            // Currently disabled in your existing code.
            //
            // If you want to enable it later:
            //
            // rebuildActionsNode(pageSchemaNode, pageCode);
            //
            // -----------------------------------------------------

            // -----------------------------------------------------
            // 7. Inject component-level actions
            // -----------------------------------------------------
            injectComponentActions(
                    pageSchemaNode,
                    pageCode
            );

            // -----------------------------------------------------
            // 8. Build DTO
            // -----------------------------------------------------
            PageJsonDTO assembledPageJson =
                    new PageJsonDTO();

            assembledPageJson.setUiPage(uiPage);
            assembledPageJson.setJsonSchema(pageSchemaNode);

            return assembledPageJson;

        } catch (RuntimeException re) {

            throw re;

        } catch (Exception ex) {

            throw new RuntimeException(
                    "FAILED TO ASSEMBLE PAGE JSON :: pageCode = "
                            + pageCode,
                    ex
            );
        }
    }

    // =================================================================
    // COMPONENT ACTIONS
    // =================================================================

    /**
     * Loads all component actions for the page,
     * groups them by componentId and injects them
     * into the JSON components.
     */
    private void injectComponentActions(
            ObjectNode pageSchemaNode,
            String pageCode) {

        List<UIComponentAction> allComponentActions =
                uiComponentActionRepository
                        .findByPageCodeOrderByComponentIdAscSequenceNoAsc(
                                pageCode
                        );

        // No actions found
        if (allComponentActions.isEmpty()) {

            // Remove any old/stale embedded actions
            clearEmbeddedActions(pageSchemaNode);

            return;
        }

        // Group actions by component ID
        Map<Long, List<UIComponentAction>> byComponentId =
                allComponentActions.stream()
                        .collect(Collectors.groupingBy(
                                UIComponentAction::getComponentId
                        ));

        // Inject actions recursively
        injectIntoComponents(
                pageSchemaNode.withArray("components"),
                byComponentId
        );
    }

    /**
     * Recursively walks through components and nested children.
     */
    private void injectIntoComponents(
            ArrayNode components,
            Map<Long, List<UIComponentAction>> byComponentId) {

        for (JsonNode componentNode : components) {

            if (!componentNode.isObject()) {
                continue;
            }

            ObjectNode component =
                    (ObjectNode) componentNode;

            long componentId =
                    component.path("id").asLong(-1);

            // Remove old action array
            component.remove("action");

            // Get actions for this component
            List<UIComponentAction> actions =
                    byComponentId.get(componentId);

            if (actions != null && !actions.isEmpty()) {

                ArrayNode actionArray =
                        component.putArray("action");

                for (UIComponentAction action : actions) {

                    ObjectNode actionEntry =
                            OBJECT_MAPPER.createObjectNode();

                    actionEntry.put(
                            "id",
                            action.getId()
                    );

                    actionEntry.put(
                            "event",
                            action.getEvent()
                    );

                    actionEntry.put(
                            "ref",
                            action.getActionRef()
                    );

                    actionEntry.put(
                            "condition",
                            action.getConditionExpr() != null
                                    ? action.getConditionExpr()
                                    : "true"
                    );

                    actionArray.add(actionEntry);
                }
            }

            // -----------------------------------------------------
            // Handle nested children
            // -----------------------------------------------------
            if (component.has("children")
                    && component.get("children").isArray()) {

                injectIntoComponents(
                        (ArrayNode) component.get("children"),
                        byComponentId
                );
            }
        }
    }

    // =================================================================
    // CLEAR OLD EMBEDDED ACTIONS
    // =================================================================

    /**
     * Removes stale action arrays from the JSON.
     */
    private void clearEmbeddedActions(
            ObjectNode root) {

        JsonNode components =
                root.path("components");

        if (components.isArray()) {

            clearEmbeddedActionsRecursive(
                    (ArrayNode) components
            );
        }
    }

    private void clearEmbeddedActionsRecursive(
            ArrayNode components) {

        for (JsonNode node : components) {

            if (!node.isObject()) {
                continue;
            }

            ObjectNode component =
                    (ObjectNode) node;

            component.remove("action");

            if (component.has("children")
                    && component.get("children").isArray()) {

                clearEmbeddedActionsRecursive(
                        (ArrayNode) component.get("children")
                );
            }
        }
    }

    // =================================================================
    // PAGE NODE
    // =================================================================

    /**
     * Updates the page metadata inside the JSON
     * using the latest UIPage database values.
     */
    private void enrichPageNode(
            ObjectNode root,
            UIPage uiPage) {

        ObjectNode pageNode;

        if (root.has("page")
                && root.get("page").isObject()) {

            pageNode =
                    (ObjectNode) root.get("page");

        } else {

            pageNode =
                    root.putObject("page");
        }

        pageNode.put(
                "pageCode",
                uiPage.getPageCode()
        );

        pageNode.put(
                "pageName",
                uiPage.getPageName()
        );

        pageNode.put(
                "isActive",
                Boolean.TRUE.equals(
                        uiPage.isActive()
                )
        );

        if (uiPage.getDescription() != null) {

            pageNode.put(
                    "description",
                    uiPage.getDescription()
            );
        }
    }

    // =================================================================
    // PAGE ACTIONS
    // =================================================================

    /**
     * Rebuilds page-level actions from the database.
     *
     * Currently not called, matching your existing implementation.
     */
    private void rebuildActionsNode(
            ObjectNode root,
            String pageCode) {

        List<UIPageAction> dbActions =
                uiPageActionRepository
                        .findByUiPagecode(pageCode);

        ObjectNode actionsNode =
                OBJECT_MAPPER.createObjectNode();

        for (UIPageAction action : dbActions) {

            ObjectNode actionNode =
                    OBJECT_MAPPER.createObjectNode();

            actionNode.put(
                    "id",
                    action.getId()
            );

            actionNode.put(
                    "type",
                    action.getActionType()
            );

            if (action.getProperties() != null
                    && !action.getProperties().isBlank()) {

                try {

                    JsonNode propertiesNode =
                            OBJECT_MAPPER.readTree(
                                    action.getProperties()
                            );

                    if (propertiesNode.isObject()) {

                        propertiesNode.fields()
                                .forEachRemaining(entry ->
                                        actionNode.set(
                                                entry.getKey(),
                                                entry.getValue()
                                        )
                                );
                    }

                } catch (Exception ignored) {

                    actionNode.put(
                            "propertiesParseError",
                            true
                    );
                }
            }

            actionsNode.set(
                    action.getActionName(),
                    actionNode
            );
        }

        root.set(
                "actions",
                actionsNode
        );
    }
}