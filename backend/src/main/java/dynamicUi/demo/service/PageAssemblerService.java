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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Assembles the complete page JSON from all related tables:
 *  - ui_page_json   → base schema (components tree, page metadata)
 *  - ui_page_action → page-level actions (stored in "actions" node)
 *  - per-component actions are already embedded in the components array
 *    by UIPageActionServiceImp.addComponentAction()
 *
 * Assembled structure returned to the frontend:
 * {
 *   "page":       { "pageCode": "...", "pageName": "...", "isActive": true },
 *   "components": [ { ...component, "action": [ { "event":"click","ref":"..." } ] } ],
 *   "actions":    { "submitAction": { "id": 5, "type": "API_CALL", "url": "..." } }
 * }
 */
@Service
@RequiredArgsConstructor
public class PageAssemblerService {

    private final UIPageJsonRepository   uiPageJsonRepository;
    private final UIPageActionRepository uiPageActionRepository;
    private final UIPageRepository       uiPageRepository;

    private final UIComponentActionRepository uiComponentActionRepository;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Assembles the full page JSON for the given pageCode.
     *
     * Steps:
     *  1. Validate the page exists and is active.
     *  2. Load the stored JSON schema from ui_page_json.
     *  3. Enrich the "page" node with live data from ui_page.
     *  4. Load page-level actions from ui_page_action and rebuild the
     *     "actions" node so it is always in sync with the DB.
     *  5. Return the assembled DTO.
     */
    public PageJsonDTO getPageAssembledByPageCode(String pageCode) {

        // 1. Validate page exists and is active
        UIPage uiPage = uiPageRepository.findByPageCode(pageCode)
                .orElseThrow(() -> new RuntimeException(
                        "PAGE NOT FOUND :: pageCode = " + pageCode));

        if (!Boolean.TRUE.equals(uiPage.getIsActive())) {
            throw new RuntimeException(
                    "PAGE IS INACTIVE :: pageCode = " + pageCode);
        }

        // 2. Load base JSON schema
        UIPageJson uiPageJson = uiPageJsonRepository.findByUiPage_PageCode(pageCode)
                .orElseThrow(() -> new RuntimeException(
                        "PAGE JSON NOT FOUND :: pageCode = " + pageCode));

        try {
            // 3. Parse schema into a mutable ObjectNode
            ObjectNode pageSchemaNode =
                    (ObjectNode) OBJECT_MAPPER.readTree(uiPageJson.getJsonSchema());

            // 4. Enrich the "page" metadata node with fresh DB values
            enrichPageNode(pageSchemaNode, uiPage);

            // 5. Ensure "components" array exists (defensive)
            if (!pageSchemaNode.has("components")
                    || !pageSchemaNode.get("components").isArray()) {
                pageSchemaNode.set("components", OBJECT_MAPPER.createArrayNode());
            }

            // 6. Rebuild "actions" node fresh from ui_page_action table
            //    so the assembled JSON is always in sync with the DB
//            no need may be turned on in future
//            rebuildActionsNode(pageSchemaNode, pageCode);

            // 7. Inject component-level actions from ui_component_action table
            //    Replaces any "action" arrays that were previously embedded in the JSON
            injectComponentActions(pageSchemaNode, pageCode);

            // . Build and return DTO
            PageJsonDTO assembledPageJson = new PageJsonDTO();
            assembledPageJson.setUiPage(uiPage);
            assembledPageJson.setJsonSchema(pageSchemaNode);

            return assembledPageJson;

        } catch (RuntimeException re) {
            throw re;
        } catch (Exception ex) {
            throw new RuntimeException(
                    "FAILED TO ASSEMBLE PAGE JSON :: pageCode = " + pageCode, ex);
        }
    }



    // ── helpers ──────────────────────────────────────────────────────────────

    /**
     * Loads all UIComponentAction rows for this page in one query,
     * groups them by componentId, then walks the components array in the
     * JSON and replaces (or creates) each component's "action" array.
     *
     * This completely replaces the old approach of storing action arrays
     * inside the JSON blob, so there is no sync issue.
     * @param pageSchemaNode ;- to store response in json
     * @param pageCode ;- for fetching all page component action
     */
    private void injectComponentActions(ObjectNode pageSchemaNode, String pageCode) {
        List<UIComponentAction> allComponentActions =
                uiComponentActionRepository
                        .findByPageCodeOrderByComponentIdAscSequenceNoAsc(pageCode);

        //because of migration from tradition to new approach fix old bug
        if (allComponentActions.isEmpty()) {
            // Still clear any stale embedded actions from the JSON
            clearEmbeddedActions(pageSchemaNode);
            return;
        }

        // Group by componentId for O(1) lookup while walking the components array
        Map<Long, List<UIComponentAction>> byComponentId = allComponentActions.stream()
                .collect(Collectors.groupingBy(UIComponentAction::getComponentId));

        injectIntoComponents(pageSchemaNode.withArray("components"), byComponentId);
    }

    /**
     * Recursively walks the components array (including nested children)
     * and injects the "action" array for each component from the DB map.
     */
    private void injectIntoComponents(ArrayNode components,
                                      Map<Long, List<UIComponentAction>> byComponentId) {
        for (JsonNode componentNode : components) {
            if (!componentNode.isObject()) continue;
            ObjectNode component = (ObjectNode) componentNode;

            long componentId = component.path("id").asLong(-1);

            // Always replace (or remove) the embedded "action" array
            component.remove("action");

            List<UIComponentAction> actions = byComponentId.get(componentId);
            if (actions != null && !actions.isEmpty()) {
                ArrayNode actionArray = component.putArray("action");
                for (UIComponentAction action : actions) {
                    ObjectNode actionEntry = OBJECT_MAPPER.createObjectNode();
                    actionEntry.put("id",        action.getId());
                    actionEntry.put("event",     action.getEvent());
                    actionEntry.put("ref",       action.getActionRef());
                    actionEntry.put("condition", action.getConditionExpr() != null
                            ? action.getConditionExpr() : "true");
                    actionArray.add(actionEntry);
                }
            }

            // Recurse into nested children
            if (component.has("children") && component.get("children").isArray()) {
                injectIntoComponents((ArrayNode) component.get("children"), byComponentId);
            }
        }
    }

    /**
     * Clears any stale "action" arrays embedded directly in the JSON
     * (from the old approach) when there are no DB rows for this page.
     */
    private void clearEmbeddedActions(ObjectNode root) {
        JsonNode components = root.path("components");
        if (components.isArray()) {
            clearEmbeddedActionsRecursive((ArrayNode) components);
        }
    }

    private void clearEmbeddedActionsRecursive(ArrayNode components) {
        for (JsonNode node : components) {
            if (!node.isObject()) continue;
            ObjectNode component = (ObjectNode) node;
            component.remove("action");
            if (component.has("children") && component.get("children").isArray()) {
                clearEmbeddedActionsRecursive((ArrayNode) component.get("children"));
            }
        }
    }


    /**
     * Merges live UIPage fields into the "page" node inside the schema
     * so the assembled JSON always reflects the current DB state.
     */
    private void enrichPageNode(ObjectNode root, UIPage uiPage) {
        ObjectNode pageNode = root.has("page") && root.get("page").isObject()
                ? (ObjectNode) root.get("page")
                : root.putObject("page");

        pageNode.put("pageCode", uiPage.getPageCode());
        pageNode.put("pageName", uiPage.getPageName());
        pageNode.put("isActive", Boolean.TRUE.equals(uiPage.getIsActive()));
        if (uiPage.getDescription() != null) {
            pageNode.put("description", uiPage.getDescription());
        }
    }

    /**
     * Replaces the "actions" node in the schema with a freshly built one
     * loaded from the ui_page_action table. The DB is the source of truth;
     * the json_schema copy is just a convenience cache for rendering.
     *
     * Action JSON structure per entry:
     *   "actionName": {
     *       "id":   <db id>,
     *       "type": "...",
     *       <all keys from the properties JSON column>
     *   }
     */
    private void rebuildActionsNode(ObjectNode root, String pageCode) {
        List<UIPageAction> dbActions = uiPageActionRepository.findByUiPagecode(pageCode);

        ObjectNode actionsNode = OBJECT_MAPPER.createObjectNode();

        for (UIPageAction action : dbActions) {
            ObjectNode actionNode = OBJECT_MAPPER.createObjectNode();
            actionNode.put("id",   action.getId());
            actionNode.put("type", action.getActionType());

            // Merge the properties JSON column (flat key/value pairs) into the node
            if (action.getProperties() != null && !action.getProperties().isBlank()) {
                try {
                    JsonNode propertiesNode = OBJECT_MAPPER.readTree(action.getProperties());
                    if (propertiesNode.isObject()) {
                        propertiesNode.fields().forEachRemaining(entry ->
                                actionNode.set(entry.getKey(), entry.getValue()));
                    }
                } catch (Exception ignored) {
                    // Malformed JSON in properties — keep "type" at minimum
                    actionNode.put("propertiesParseError", true);
                }
            }

            actionsNode.set(action.getActionName(), actionNode);
        }

        root.set("actions", actionsNode);
    }
}