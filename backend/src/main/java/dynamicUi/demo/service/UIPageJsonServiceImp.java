package dynamicUi.demo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dynamicUi.demo.repoistory.UIPageRepository;
import dynamicUi.demo.service.inter.UIPageJsonService;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import dynamicUi.demo.entity.UIComponent;
import dynamicUi.demo.entity.UIEntityMapping;
import dynamicUi.demo.entity.UIPage;
import dynamicUi.demo.entity.UIPageJson;
import dynamicUi.demo.repoistory.UIPageJsonRepository;
import dynamicUi.demo.repoistory.UIEntityMappingRepository;
import dynamicUi.demo.repoistory.UiComponentRepository;

@Service
public class UIPageJsonServiceImp implements UIPageJsonService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final UIPageJsonRepository uiPageJsonRepository;
    private final UiComponentRepository uiComponentRepository;
    private final UIEntityMappingRepository uiEntityMappingRepository;
    private  final UIPageRepository uiPageRepository;

    public UIPageJsonServiceImp(
            UIPageJsonRepository uiPageJsonRepository,
            UiComponentRepository uiComponentRepository,
            UIEntityMappingRepository uiEntityMappingRepository, UIPageRepository uiPageRepository
    ) {
        this.uiPageJsonRepository = uiPageJsonRepository;
        this.uiComponentRepository = uiComponentRepository;
        this.uiEntityMappingRepository = uiEntityMappingRepository;
        this.uiPageRepository = uiPageRepository;
    }

    @Override
    public UIPageJson getByPageCode(String pageCode) {

        UIPage page = uiPageRepository.findByPageCode(pageCode)
                .orElseThrow(() ->
                        new RuntimeException("Page not found"));

        if (!Boolean.TRUE.equals(page.getIsActive())) {
            throw new RuntimeException("Page is inactive");
        }

        return uiPageJsonRepository.findByUiPage_PageCode(pageCode)
                .orElseThrow(() ->
                        new RuntimeException("Page schema not found"));
    }
    @Override
    public void syncPageJson(String pageCode) {
        ObjectNode root = parseOrCreateRoot(pageCode, null);
        List<UIComponent> components = uiComponentRepository.findByUiPage_PageCodeAndIsActiveTrueOrderBySequenceNo(pageCode);

        Map<Long, ObjectNode> nodeById = new HashMap<>();
        ArrayNode rootComponents = OBJECT_MAPPER.createArrayNode();

        for (UIComponent component : components) {
            nodeById.put(component.getId(), toComponentNode(component));
        }

        for (UIComponent component : components) {
            ObjectNode node = nodeById.get(component.getId());
            Long parentId = component.getParentComponentId();

            if (parentId == null || !nodeById.containsKey(parentId)) {
                rootComponents.add(node);
                continue;
            }

            ObjectNode parentNode = nodeById.get(parentId);
            ArrayNode children = parentNode.has("children") && parentNode.get("children").isArray()
                    ? (ArrayNode) parentNode.get("children")
                    : parentNode.putArray("children");
            children.add(node);
        }

        root.set("components", rootComponents);
        savePageJson(pageCode, root, null);
    }

    private ObjectNode parseOrCreateRoot(String pageCode, String pageName) {
        UIPageJson pageJson = uiPageJsonRepository.findByUiPage_PageCode(pageCode).orElse(null);
        String schema = pageJson != null ? pageJson.getJsonSchema() : null;
        if (schema != null && !schema.isBlank()) {
            try {
                JsonNode parsed = OBJECT_MAPPER.readTree(schema);
                if (parsed.isObject()) {
                    ObjectNode root = (ObjectNode) parsed;
                    ensurePageNode(root, pageCode, pageName);
                    ensureComponentsNode(root);
                    return root;
                }
            } catch (JsonProcessingException ignored) {
                // fall through to create empty schema
            }
        }

        ObjectNode root = OBJECT_MAPPER.createObjectNode();
        ensurePageNode(root, pageCode, pageName);
        root.set("components", OBJECT_MAPPER.createArrayNode());
        return root;
    }

    private void ensurePageNode(ObjectNode root, String pageCode, String pageName) {
        ObjectNode pageNode = root.has("page") && root.get("page").isObject()
                ? (ObjectNode) root.get("page")
                : root.putObject("page");
        pageNode.put("pageCode", pageCode);
        if (pageName != null) {
            pageNode.put("pageName", pageName);
        } else if (!pageNode.has("pageName") && !pageNode.has("title")) {
            pageNode.put("pageName", pageCode);
        }
    }

    private void ensureComponentsNode(ObjectNode root) {
        if (!root.has("components") || !root.get("components").isArray()) {
            root.set("components", OBJECT_MAPPER.createArrayNode());
        }
    }

    private ObjectNode toComponentNode(UIComponent component) {
        ObjectNode componentNode = OBJECT_MAPPER.createObjectNode();
        componentNode.put("id", component.getId());
        componentNode.put("name", component.getComponentName());
        componentNode.put("type", normalizeComponentType(component.getComponentType()));
        componentNode.put("sequence", component.getSequenceNo());

        ObjectNode propertiesNode = OBJECT_MAPPER.createObjectNode();
        if (component.getLabelName() != null) {
            propertiesNode.put("label", component.getLabelName());
        }
        if (component.getPlaceholder() != null) {
            propertiesNode.put("placeholder", component.getPlaceholder());
        }
        if (component.getIsVisible() != null) {
            propertiesNode.put("visible", component.getIsVisible());
        }
        if (component.getIsDisabled() != null) {
            propertiesNode.put("disabled", component.getIsDisabled());
        }
        if (component.getIsRequired() != null) {
            propertiesNode.put("required", component.getIsRequired());
        }

        if ("button".equalsIgnoreCase(component.getComponentType())) {
            if (!propertiesNode.has("text") && component.getLabelName() != null) {
                propertiesNode.put("text", component.getLabelName());
            }
        }

        if (("select".equalsIgnoreCase(component.getComponentType()) || "radio".equalsIgnoreCase(component.getComponentType()))) {
            // Store only lookup schema; frontend will fetch labels/values dynamically from the API at render time
            ObjectNode lookupNode = OBJECT_MAPPER.createObjectNode();
            lookupNode.put("apiUrl", "/api/ui/lookups/component/" + component.getId());
            lookupNode.put("method", "GET");
            componentNode.set("lookup", lookupNode);
        }

        uiEntityMappingRepository.findByUiComponent_Id(component.getId())
                .ifPresent(mapping -> componentNode.set("mapping", toMappingNode(mapping)));

        componentNode.set("properties", propertiesNode);


        return componentNode;
    }

    private ObjectNode toMappingNode(UIEntityMapping mapping) {
        ObjectNode mappingNode = OBJECT_MAPPER.createObjectNode();
        String mappingType = mapping.getMappingType();
        if (mappingType == null || mappingType.isBlank()) {
            mappingType = "ENTITY";
        }
        mappingNode.put("type", mappingType);

        if ("API".equalsIgnoreCase(mappingType)) {
            if (mapping.getSource() != null && !mapping.getSource().isBlank()) {
                mappingNode.put("source", mapping.getSource());
            }
            if (mapping.getResponsePath() != null && !mapping.getResponsePath().isBlank()) {
                mappingNode.put("responsePath", mapping.getResponsePath());
            }
            return mappingNode;
        }

        if (mapping.getAttributeName() != null && !mapping.getAttributeName().isBlank()) {
            mappingNode.put("source", mapping.getAttributeName());
        } else if (mapping.getColumnName() != null && !mapping.getColumnName().isBlank()) {
            mappingNode.put("source", mapping.getColumnName());
        } else if (mapping.getTableName() != null && !mapping.getTableName().isBlank()) {
            mappingNode.put("source", mapping.getTableName());
        }

        if (mapping.getTableName() != null && !mapping.getTableName().isBlank()
                && mapping.getColumnName() != null && !mapping.getColumnName().isBlank()) {
            mappingNode.put("source", mapping.getTableName() + '.' + mapping.getColumnName());
        }

        if (mapping.getColumnName() != null && !mapping.getColumnName().isBlank()) {
            mappingNode.put("target", mapping.getColumnName());
        }

        if (mapping.getTableName() != null && !mapping.getTableName().isBlank()) {
            mappingNode.put("expression", mapping.getTableName());
        }

        if (mapping.getDisplayName() != null && !mapping.getDisplayName().isBlank()) {
            mappingNode.put("defaultValue", mapping.getDisplayName());
        }

        ArrayNode dependencies = OBJECT_MAPPER.createArrayNode();
        if (Boolean.TRUE.equals(mapping.getIsRequired())) {
            dependencies.add("required");
        }
        if (Boolean.TRUE.equals(mapping.getIsFilterable())) {
            dependencies.add("filterable");
        }
        if (!dependencies.isEmpty()) {
            mappingNode.set("dependencies", dependencies);
        }

        return mappingNode;
    }

    private String normalizeComponentType(String componentType) {
        if (componentType == null) {
            return null;
        }
        if ("dropdown".equalsIgnoreCase(componentType)) {
            return "select";
        }
        return componentType;
    }

    private void savePageJson(String pageCode, ObjectNode root, UIPage uiPage) {
        UIPageJson pageJson = uiPageJsonRepository.findByUiPage_PageCode(pageCode)
                .orElseGet(() -> {
                    UIPageJson newPageJson = new UIPageJson();
                    if (uiPage != null) {
                        newPageJson.setUiPage(uiPage);
                    } else {
                        UIPage placeholderPage = new UIPage();
                        placeholderPage.setPageCode(pageCode);
                        newPageJson.setUiPage(placeholderPage);
                    }
                    return newPageJson;
                });
        pageJson.setJsonSchema(root.toString());
        uiPageJsonRepository.save(pageJson);
    }
}