package dynamicUi.demo.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import dynamicUi.demo.dto.GlobalUiRequest;
import dynamicUi.demo.entity.GlobalUiConfig;
import dynamicUi.demo.repoistory.GlobalUiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GlobalUiService {

    private final GlobalUiRepository repository;
    private final ObjectMapper mapper;

    private static final String STYLE_SCOPE = "GLOBAL"; // styling has no facility, so use a fixed key
    private static final String STYLE_TYPE = "NAVBAR_STYLE";
    private static final String COMPONENTS_TYPE = "NAVBAR_COMPONENTS";

    public void saveStyle(JsonNode styleJson) {
        GlobalUiConfig config =
                repository.findByFacilityIdAndTypeAndIsActiveTrue(STYLE_SCOPE, STYLE_TYPE)
                        .orElse(new GlobalUiConfig());

        config.setFacilityId(STYLE_SCOPE);
        config.setType(STYLE_TYPE);
        config.setConfigJson(mapper.writeValueAsString(styleJson));
        config.setIsActive(true);

        repository.save(config);
    }

    public JsonNode getStyle() {
        return repository
                .findByFacilityIdAndTypeAndIsActiveTrue(STYLE_SCOPE, STYLE_TYPE)
                .map(config -> mapper.readTree(config.getConfigJson()))
                .orElse(null);
    }

    public void saveComponents(String facilityId, JsonNode componentsJson) {
        GlobalUiConfig config =
                repository.findByFacilityIdAndTypeAndIsActiveTrue(facilityId, COMPONENTS_TYPE)
                        .orElse(new GlobalUiConfig());

        config.setFacilityId(facilityId);
        config.setType(COMPONENTS_TYPE);
        config.setConfigJson(mapper.writeValueAsString(componentsJson));
        config.setIsActive(true);

        repository.save(config);
    }

    public JsonNode getComponents(String facilityId) {
        return repository
                .findByFacilityIdAndTypeAndIsActiveTrue(facilityId, COMPONENTS_TYPE)
                .map(config -> mapper.readTree(config.getConfigJson()))
                .orElse(null);
    }
}