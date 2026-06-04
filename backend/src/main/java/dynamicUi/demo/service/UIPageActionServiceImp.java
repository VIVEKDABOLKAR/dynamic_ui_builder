package dynamicUi.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dynamicUi.demo.dto.ComponentActionRequest;
import dynamicUi.demo.entity.UIPage;
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
    private final UIPageJsonRepository uiPageJsonRepository;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public UIPageAction create(String pageCode, UIPageAction action) {
        // store it in action table
        UIPageAction uiPageAction = UIPageAction.builder()
                .actionName(action.getActionName())
                .actionType(action.getActionType())
                .properties(action.getProperties())
                .uiPagecode(action.getUiPagecode())
                .build();

        repository.save(uiPageAction);

        // store in page json
        UIPageJson pageJson = uiPageJsonRepository
                .findByUiPage_PageCode(pageCode)
                .orElseThrow(() -> new RuntimeException("Page JSON not found"));

        try {
            ObjectNode root = (ObjectNode) OBJECT_MAPPER.readTree(pageJson.getJsonSchema());

            ObjectNode actionsNode;
            if (root.has("actions")) {
                actionsNode = (ObjectNode) root.get("actions");
            } else {
                actionsNode = OBJECT_MAPPER.createObjectNode();
                root.set("actions", actionsNode);
            }

            ObjectNode actionNode = OBJECT_MAPPER.createObjectNode();
            actionNode.put("type", action.getActionType());

            JsonNode propertiesNode = OBJECT_MAPPER.readTree(action.getProperties());
            propertiesNode.fields().forEachRemaining(wrapper ->
                    actionNode.set(wrapper.getKey(), wrapper.getValue()));

            actionsNode.set(action.getActionName(), actionNode);

            pageJson.setJsonSchema(
                    OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(root));

            uiPageJsonRepository.save(pageJson);

            return uiPageAction;

        } catch (Exception e) {
            throw new RuntimeException("Failed to update page json", e);
        }
    }

    @Override
    public UIPageAction update(Long id, UIPageAction uiPageAction) {
        UIPageAction existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("UIPageAction not found with id: " + id));

        String oldActionName = existing.getActionName();
        String pageCode = existing.getUiPagecode();

        existing.setActionName(uiPageAction.getActionName());
        existing.setActionType(uiPageAction.getActionType());
        existing.setProperties(uiPageAction.getProperties());

        UIPageAction saved = repository.save(existing);

        // update page json
        uiPageJsonRepository.findByUiPage_PageCode(pageCode).ifPresent(pageJson -> {
            try {
                ObjectNode root = (ObjectNode) OBJECT_MAPPER.readTree(pageJson.getJsonSchema());
                ObjectNode actionsNode = root.has("actions")
                        ? (ObjectNode) root.get("actions")
                        : OBJECT_MAPPER.createObjectNode();

                // remove old key if name changed
                if (!oldActionName.equals(uiPageAction.getActionName())) {
                    actionsNode.remove(oldActionName);
                }

                ObjectNode actionNode = OBJECT_MAPPER.createObjectNode();
                actionNode.put("type", uiPageAction.getActionType());
                JsonNode propertiesNode = OBJECT_MAPPER.readTree(uiPageAction.getProperties());
                propertiesNode.fields().forEachRemaining(w -> actionNode.set(w.getKey(), w.getValue()));
                actionsNode.set(uiPageAction.getActionName(), actionNode);
                root.set("actions", actionsNode);

                pageJson.setJsonSchema(
                        OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(root));
                uiPageJsonRepository.save(pageJson);
            } catch (Exception e) {
                throw new RuntimeException("Failed to update page json on edit", e);
            }
        });

        return saved;
    }

    @Override
    public UIPageAction getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("UIPageAction not found with id: " + id));
    }

    @Override
    public List<UIPageAction> getByPageCode(String pageCode) {
        return repository.findByUiPagecode(pageCode);
    }

    @Override
    public void delete(Long id) {
        UIPageAction existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("UIPageAction not found with id: " + id));

        String actionName = existing.getActionName();
        String pageCode = existing.getUiPagecode();

        repository.deleteById(id);

        // remove from page json
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
    public void addComponentAction(
            String pageCode,
            ComponentActionRequest request) {

        UIPageJson page =
                uiPageJsonRepository.findByUiPage_PageCode(pageCode)
                        .orElseThrow(() ->
                                new RuntimeException("Page not found"));

        try {

            JsonNode root =
                    OBJECT_MAPPER.readTree(page.getJsonSchema());





                JsonNode components =
                        root.path("components");

                for (JsonNode component : components) {

                    Long componentId =
                            component.path("id").asLong();

                    if (componentId.equals(
                            request.getComponentId())) {

                        ObjectNode componentNode =
                                (ObjectNode) component;

                        ArrayNode actionArray;

                        if (componentNode.has("action")) {

                            actionArray =
                                    (ArrayNode)
                                            componentNode.get("action");

                        } else {

                            actionArray =
                                    OBJECT_MAPPER.createArrayNode();

                            componentNode.set(
                                    "action",
                                    actionArray);
                        }

                        ObjectNode action =
                                OBJECT_MAPPER.createObjectNode();

                        action.put(
                                "event",
                                request.getEvent());

                        action.put(
                                "ref",
                                request.getRef());

                        action.put(
                                "condition",
                                request.getCondition() == null
                                        ? "true"
                                        : request.getCondition());

                        actionArray.add(action);

                        page.setJsonSchema(
                                OBJECT_MAPPER.writerWithDefaultPrettyPrinter()
                                        .writeValueAsString(root));

                        uiPageJsonRepository.save(page);

                        return;
                    }
                }


            throw new RuntimeException(
                    "Component not found");

        } catch (Exception ex) {

            throw new RuntimeException(
                    "Failed to update component action",
                    ex);
        }
    }
}
