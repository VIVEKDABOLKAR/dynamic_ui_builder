package dynamicUi.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
    public UIPageAction create(
            String pageCode,
            UIPageAction action) {

        UIPageJson pageJson = uiPageJsonRepository
                .findByUiPage_PageCode(pageCode)
                .orElseThrow(() -> new RuntimeException("Page JSON not found"));

        try {

            ObjectNode root =
                    (ObjectNode) OBJECT_MAPPER.readTree(pageJson.getJsonSchema());

            ObjectNode actionsNode;

            if (root.has("actions")) {
                actionsNode = (ObjectNode) root.get("actions");
            } else {
                actionsNode = OBJECT_MAPPER.createObjectNode();
                root.set("actions", actionsNode);
            }

            ObjectNode actionNode = OBJECT_MAPPER.createObjectNode();

            actionNode.put("type", action.getActionType());

            JsonNode propertiesNode =
                    OBJECT_MAPPER.readTree(action.getProperties());

            propertiesNode.fields().forEachRemaining(wrapper -> {
                actionNode.set(wrapper.getKey(), wrapper.getValue());
            });

            actionsNode.set(action.getActionName(), actionNode);

            pageJson.setJsonSchema(
                    OBJECT_MAPPER.writerWithDefaultPrettyPrinter()
                            .writeValueAsString(root));

            uiPageJsonRepository.save(pageJson);

            return action;

        } catch (Exception e) {
            throw new RuntimeException("Failed to update page json", e);
        }
    }
//    @Override
//    public UIPageAction update(Long id, UIPageAction uiPageAction) {
//
//        UIPageAction existing = repository.findById(id)
//                .orElseThrow(() -> new RuntimeException("UIPageAction not found with id: " + id));
//
//        existing.setActionName(uiPageAction.getActionName());
//        existing.setActionType(uiPageAction.getActionType());
//        existing.setProperties(uiPageAction.getProperties());
//
//        return repository.save(existing);
//    }
//
//    @Override
//    public UIPageAction getById(Long id) {
//        return repository.findById(id)
//                .orElseThrow(() -> new RuntimeException("UIPageAction not found with id: " + id));
//    }
//
//    @Override
//    public List<UIPageAction> getAll() {
//        return repository.findAll();
//    }
//
//    @Override
//    public List<UIPageAction> getByPageId(Long pageId) {
//        return repository.findByUiPageId(pageId);
//    }
//
//    @Override
//    public void delete(Long id) {
//
//        if (!repository.existsById(id)) {
//            throw new RuntimeException("UIPageAction not found with id: " + id);
//        }
//
//        repository.deleteById(id);
//    }
}
