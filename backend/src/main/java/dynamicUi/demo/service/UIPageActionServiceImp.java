package dynamicUi.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dynamicUi.demo.dto.ComponentActionRequest;
import dynamicUi.demo.entity.UIPageAction;
import dynamicUi.demo.entity.UIPageJson;
import dynamicUi.demo.repoistory.UIPageActionRepository;
import dynamicUi.demo.repoistory.UIPageJsonRepository;
import dynamicUi.demo.service.inter.UIPageActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UIPageActionServiceImp implements UIPageActionService {

    private final UIPageActionRepository repository;
    private final UIPageJsonRepository   uiPageJsonRepository;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public UIPageAction create(String pageCode, UIPageAction action) {

        // FIX: original code used action.getUiPagecode() from the request body,
        // which could be null if the client didn't set it. The controller already
        // calls uiPageAction.setUiPagecode(pageCode) before reaching here, but
        // using the explicit path parameter is safer and more explicit.
        UIPageAction uiPageAction = UIPageAction.builder()
                .actionName(action.getActionName())
                .actionType(action.getActionType())
                .properties(action.getProperties())
                .uiPagecode(pageCode)   // ← always use the path param
                .build();

        repository.save(uiPageAction);

        // Mirror into page JSON cache for fast rendering
        syncActionToPageJson(
                pageCode,
                uiPageAction.getActionName(),
                uiPageAction.getActionType(),
                uiPageAction.getProperties(),
                null);

        return uiPageAction;
    }

    @Override
    public UIPageAction update(Long id, UIPageAction uiPageAction) {
        UIPageAction existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("UIPageAction not found: " + id));

        String oldActionName = existing.getActionName();
        String pageCode      = existing.getUiPagecode();

        existing.setActionName(uiPageAction.getActionName());
        existing.setActionType(uiPageAction.getActionType());
        existing.setProperties(uiPageAction.getProperties());

        UIPageAction saved = repository.save(existing);

        // Update page JSON cache
        syncActionToPageJson(
                pageCode,
                uiPageAction.getActionName(),
                uiPageAction.getActionType(),
                uiPageAction.getProperties(),
                oldActionName);

        return saved;
    }

    @Override
    public UIPageAction getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("UIPageAction not found: " + id));
    }

    @Override
    public List<UIPageAction> getByPageCode(String pageCode) {
        return repository.findByUiPagecode(pageCode);
    }

    @Override
    public void delete(Long id) {
        UIPageAction existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("UIPageAction not found: " + id));

        String actionName = existing.getActionName();
        String pageCode   = existing.getUiPagecode();

        repository.deleteById(id);

        // Remove from page JSON cache
        uiPageJsonRepository.findByUiPage_PageCode(pageCode).ifPresent(pageJson -> {
            try {
                ObjectNode root = (ObjectNode) OBJECT_MAPPER.readTree(pageJson.getJsonSchema());
                if (root.has("actions")) {
                    ((ObjectNode) root.get("actions")).remove(actionName);
                    pageJson.setJsonSchema(
                            OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(root));
                    uiPageJsonRepository.save(pageJson);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to remove action from page json", e);
            }
        });
    }

    @Override
    public void addComponentAction(String pageCode, ComponentActionRequest request) {

        // FIX: original code had ambiguous try-without-scope, unnecessary blank lines,
        // and most importantly tried to use a JsonNode (immutable) reference to write
        // back to uiPageJsonRepository — the root must be cast to ObjectNode.

        UIPageJson page = uiPageJsonRepository.findByUiPage_PageCode(pageCode)
                .orElseThrow(() -> new RuntimeException("Page JSON not found: " + pageCode));

        try {
            // FIX: cast to ObjectNode so we can mutate the tree and serialize it back
            ObjectNode root       = (ObjectNode) OBJECT_MAPPER.readTree(page.getJsonSchema());
            JsonNode   components = root.path("components");

            if (!components.isArray()) {
                throw new RuntimeException("Components node is missing or not an array");
            }

            for (JsonNode componentNode : components) {
                long componentId = componentNode.path("id").asLong(-1);
                if (componentId != request.getComponentId()) {
                    continue;
                }

                // Found the target component — now mutate it
                ObjectNode mutableComponent = (ObjectNode) componentNode;

                ArrayNode actionArray = mutableComponent.has("action")
                        && mutableComponent.get("action").isArray()
                        ? (ArrayNode) mutableComponent.get("action")
                        : mutableComponent.putArray("action");

                ObjectNode actionEntry = OBJECT_MAPPER.createObjectNode();
                actionEntry.put("event",     request.getEvent());
                actionEntry.put("ref",       request.getRef());
                actionEntry.put("condition", request.getCondition() != null
                        ? request.getCondition() : "true");

                actionArray.add(actionEntry);

                page.setJsonSchema(
                        OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(root));
                uiPageJsonRepository.save(page);
                return;
            }

            throw new RuntimeException(
                    "Component id " + request.getComponentId() + " not found in page: " + pageCode);

        } catch (RuntimeException re) {
            throw re;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to update component action", ex);
        }
    }

    // ── private helpers ───────────────────────────────────────────────────────

    /**
     * Writes / updates a single action entry in the page JSON cache.
     * If oldActionName is provided and differs from actionName, the old key is removed.
     */
    private void syncActionToPageJson(String pageCode, String actionName,
                                      String actionType, String properties,
                                      String oldActionName) {
        uiPageJsonRepository.findByUiPage_PageCode(pageCode).ifPresent(pageJson -> {
            try {
                ObjectNode root        = (ObjectNode) OBJECT_MAPPER.readTree(pageJson.getJsonSchema());
                ObjectNode actionsNode = root.has("actions")
                        ? (ObjectNode) root.get("actions")
                        : OBJECT_MAPPER.createObjectNode();

                if (oldActionName != null && !oldActionName.equals(actionName)) {
                    actionsNode.remove(oldActionName);
                }

                actionsNode.set(actionName, buildActionNode(actionType, properties));
                root.set("actions", actionsNode);

                pageJson.setJsonSchema(
                        OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(root));
                uiPageJsonRepository.save(pageJson);
            } catch (Exception e) {
                throw new RuntimeException("Failed to sync action to page json", e);
            }
        });
    }

    /** Builds:  { "type": "...", ...propertiesFields } */
    private ObjectNode buildActionNode(String actionType, String properties) {
        ObjectNode actionNode = OBJECT_MAPPER.createObjectNode();
        actionNode.put("type", actionType);

        if (properties != null && !properties.isBlank()) {
            try {
                JsonNode propertiesNode = OBJECT_MAPPER.readTree(properties);
                if (propertiesNode.isObject()) {
                    propertiesNode.fields().forEachRemaining(e ->
                            actionNode.set(e.getKey(), e.getValue()));
                }
            } catch (Exception ignored) {
                // Keep just the type if properties JSON is malformed
            }
        }
        return actionNode;
    }
}