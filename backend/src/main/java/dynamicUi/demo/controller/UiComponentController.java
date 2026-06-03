package dynamicUi.demo.controller;

import dynamicUi.demo.dto.UIComponentDTO;
import dynamicUi.demo.dto.UIEntityMappingDTO;
import dynamicUi.demo.service.inter.UiComponentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/components")
@CrossOrigin("*")
public class UiComponentController {

    private final UiComponentService uiComponentService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UiComponentController(UiComponentService uiComponentService) {
        this.uiComponentService = uiComponentService;
    }

    @PostMapping
    public UIComponentDTO createComponent(
            @RequestBody Map<String, Object> requestBody
    ) {
        Object componentValue = requestBody.containsKey("component") ? requestBody.get("component") : requestBody;
        Object mappingValue = requestBody.containsKey("mappingValues")
            ? requestBody.get("mappingValues")
            : componentValue instanceof Map<?, ?> componentMap && componentMap.containsKey("mappingValues")
            ? componentMap.get("mappingValues")
            : null;

        Map<String, Object> normalizedComponentMap = componentValue instanceof Map<?, ?> componentMap
            ? objectMapper.convertValue(componentMap, new TypeReference<LinkedHashMap<String, Object>>() {})
            : new LinkedHashMap<>();

        if (!normalizedComponentMap.containsKey("lookupValues") && requestBody.containsKey("lookupValues")) {
            normalizedComponentMap.put("lookupValues", requestBody.get("lookupValues"));
        }

        UIComponentDTO componentDTO = objectMapper.convertValue(normalizedComponentMap, UIComponentDTO.class);
        UIEntityMappingDTO mappingDTO = mappingValue != null
            ? objectMapper.convertValue(mappingValue, UIEntityMappingDTO.class)
                : null;

        return uiComponentService.createComponent(componentDTO, mappingDTO);
    }

    @GetMapping("/page/{pageCode}")
    public List<UIComponentDTO> getComponentsByPage(
            @PathVariable String pageCode
    ) {
        return uiComponentService.getComponentsByPage(pageCode);
    }

    @GetMapping("/{id}")
    public UIComponentDTO getComponentById(
            @PathVariable Long id
    ) {
        return uiComponentService.getComponentById(id);
    }

    @PutMapping("/{id}")
    public UIComponentDTO updateComponent(
            @PathVariable Long id,
            @RequestBody Map<String, Object> requestBody
    ) {
        Object componentValue = requestBody.containsKey("component") ? requestBody.get("component") : requestBody;
        Object mappingValue = requestBody.containsKey("mappingValues")
            ? requestBody.get("mappingValues")
            : componentValue instanceof Map<?, ?> componentMap && componentMap.containsKey("mappingValues")
            ? componentMap.get("mappingValues")
            : null;

        Map<String, Object> normalizedComponentMap = componentValue instanceof Map<?, ?> componentMap
            ? objectMapper.convertValue(componentMap, new TypeReference<LinkedHashMap<String, Object>>() {})
            : new LinkedHashMap<>();

        if (!normalizedComponentMap.containsKey("lookupValues") && requestBody.containsKey("lookupValues")) {
            normalizedComponentMap.put("lookupValues", requestBody.get("lookupValues"));
        }

        UIComponentDTO componentDTO = objectMapper.convertValue(normalizedComponentMap, UIComponentDTO.class);
        UIEntityMappingDTO mappingDTO = mappingValue != null
            ? objectMapper.convertValue(mappingValue, UIEntityMappingDTO.class)
            : null;

        return uiComponentService.updateComponent(id, componentDTO, mappingDTO);
    }

    @DeleteMapping("/{id}")
    public String deleteComponent(
            @PathVariable Long id
    ) {

        uiComponentService.deleteComponent(id);

        return "Component deleted successfully";
    }
}
