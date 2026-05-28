package dynamicUi.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import dynamicUi.demo.entity.UIComponent;
import dynamicUi.demo.entity.UILookup;
import dynamicUi.demo.entity.UIPage;
import dynamicUi.demo.entity.UIPageJson;
import dynamicUi.demo.repoistory.UIPageJsonRepository;

import java.util.List;

@Service
public class UIPageJsonServiceImp implements UIPageJsonService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final UIPageJsonRepository uiPageJsonRepository;

    public UIPageJsonServiceImp(UIPageJsonRepository uiPageJsonRepository) {
        this.uiPageJsonRepository = uiPageJsonRepository;
    }

    @Override
    public UIPageJson getByPageCode(String pageCode) {
        return uiPageJsonRepository.findByUiPage_PageCode(pageCode)
            .orElseGet(() -> {
                UIPageJson j = UIPageJson.builder().id(-1L).build();
                UIPage p = new UIPage();
                p.setPageCode(pageCode);
                j.setUiPage(p);
                return j;
            });
    }

    @Override
    public void addComponentToJson(String pageCode, UIComponent component, List<UILookup> lookups) {
        // loads existing page JSON or creates empty JSON
        ObjectNode root = parseOrCreateRoot(pageCode, component.getUiPage() != null ? component.getUiPage().getPageName() : null);
        ArrayNode componentsArray = root.withArray("components");
        componentsArray.add(toComponentNode(component, lookups));
        savePageJson(pageCode, root, component.getUiPage());
    }

    @Override
    public void updateComponentInJson(String pageCode, UIComponent component, List<UILookup> lookups) {
        ObjectNode root = parseOrCreateRoot(pageCode, component.getUiPage() != null ? component.getUiPage().getPageName() : null);
        ArrayNode componentsArray = root.withArray("components");

        boolean updated = false;
        for (int i = 0; i < componentsArray.size(); i++) {
            JsonNode node = componentsArray.get(i);
            if (node.has("id") && node.get("id").asLong() == component.getId()) {
                componentsArray.set(i, toComponentNode(component, lookups));
                updated = true;
                break;
            }
        }

        if (!updated) {
            componentsArray.add(toComponentNode(component, lookups));
        }

        savePageJson(pageCode, root, component.getUiPage());
    }

    @Override
    public void removeComponentFromJson(String pageCode, Long componentId) {
        ObjectNode root = parseOrCreateRoot(pageCode, null);
        ArrayNode componentsArray = root.withArray("components");
        ArrayNode filtered = OBJECT_MAPPER.createArrayNode();

        for (JsonNode node : componentsArray) {
            if (node.has("id") && node.get("id").asLong() == componentId) {
                continue;
            }
            filtered.add(node);
        }

        root.set("components", filtered);
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

    private ObjectNode toComponentNode(UIComponent component, List<UILookup> lookups) {
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

        String properties = component.getProperties();
        if (properties != null && !properties.isBlank()) {
            try {
                JsonNode parsedProperties = OBJECT_MAPPER.readTree(properties);
                if (parsedProperties.isObject()) {
                    parsedProperties.fields().forEachRemaining(entry -> propertiesNode.set(entry.getKey(), entry.getValue()));
                }
            } catch (JsonProcessingException e) {
                // ignore invalid properties JSON and keep base properties
            }
        }

        if ("button".equalsIgnoreCase(component.getComponentType())) {
            if (!propertiesNode.has("text") && component.getLabelName() != null) {
                propertiesNode.put("text", component.getLabelName());
            }
        }

        if (lookups != null && !lookups.isEmpty() && ("select".equalsIgnoreCase(component.getComponentType()) || "radio".equalsIgnoreCase(component.getComponentType()))) {
            ArrayNode optionsArray = OBJECT_MAPPER.createArrayNode();
            for (UILookup lookup : lookups) {
                ObjectNode option = OBJECT_MAPPER.createObjectNode();
                option.put("label", lookup.getDisplayValue());
                option.put("value", lookup.getLookupValue());
                optionsArray.add(option);
            }
            propertiesNode.set("options", optionsArray);
        }

        componentNode.set("properties", propertiesNode);

        return componentNode;
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