package dynamicUi.demo.service;

import dynamicUi.demo.entity.GlobalUiConfig;
import dynamicUi.demo.repoistory.GlobalUiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link GlobalUiService}.
 *
 * Uses a REAL ObjectMapper instead of mocking it — writeValueAsString/
 * readTree round-tripping is exactly the behavior worth verifying here,
 * and stubbing it would just restate the implementation.
 */
@ExtendWith(MockitoExtension.class)
class GlobalUiServiceTest {

    @Mock
    private GlobalUiRepository repository;

    private final ObjectMapper mapper = new ObjectMapper();

    private GlobalUiService globalUiService;

    @BeforeEach
    void setUp() {
        globalUiService = new GlobalUiService(repository, mapper);
    }

    private ObjectNode sampleStyleJson() {
        ObjectNode node = mapper.createObjectNode();
        node.put("backgroundColor", "#123456");
        node.put("logoUrl", "/logo.png");
        return node;
    }

    // ── saveStyle ────────────────────────────────────────────────────────

    @Test
    @DisplayName("saveStyle creates a new GLOBAL/NAVBAR_STYLE config when none exists")
    void saveStyleCreatesNewConfigWhenNoneExists() {
        when(repository.findByFacilityIdAndTypeAndIsActiveTrue("GLOBAL", "NAVBAR_STYLE"))
                .thenReturn(Optional.empty());

        ObjectNode styleJson = sampleStyleJson();
        globalUiService.saveStyle(styleJson);

        ArgumentCaptor<GlobalUiConfig> captor = ArgumentCaptor.forClass(GlobalUiConfig.class);
        verify(repository).save(captor.capture());

        GlobalUiConfig saved = captor.getValue();
        assertThat(saved.getFacilityId()).isEqualTo("GLOBAL");
        assertThat(saved.getType()).isEqualTo("NAVBAR_STYLE");
        assertThat(saved.getIsActive()).isTrue();
        assertThat(mapper.readTree(saved.getConfigJson())).isEqualTo(styleJson);
    }

    @Test
    @DisplayName("saveStyle updates the existing config in place rather than creating a duplicate")
    void saveStyleUpdatesExistingConfigWhenPresent() {
        GlobalUiConfig existing = GlobalUiConfig.builder()
                .id(5L).facilityId("GLOBAL").type("NAVBAR_STYLE")
                .configJson("{}").isActive(true).build();
        when(repository.findByFacilityIdAndTypeAndIsActiveTrue("GLOBAL", "NAVBAR_STYLE"))
                .thenReturn(Optional.of(existing));

        ObjectNode styleJson = sampleStyleJson();
        globalUiService.saveStyle(styleJson);

        ArgumentCaptor<GlobalUiConfig> captor = ArgumentCaptor.forClass(GlobalUiConfig.class);
        verify(repository).save(captor.capture());

        assertThat(captor.getValue().getId()).isEqualTo(5L); // same row, not a new one
        assertThat(mapper.readTree(captor.getValue().getConfigJson())).isEqualTo(styleJson);
    }

    // ── getStyle ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("getStyle returns the parsed JSON when a config is present")
    void getStyleReturnsParsedJsonWhenPresent() {
        ObjectNode styleJson = sampleStyleJson();
        GlobalUiConfig config = GlobalUiConfig.builder()
                .facilityId("GLOBAL").type("NAVBAR_STYLE")
                .configJson(mapper.writeValueAsString(styleJson))
                .isActive(true).build();
        when(repository.findByFacilityIdAndTypeAndIsActiveTrue("GLOBAL", "NAVBAR_STYLE"))
                .thenReturn(Optional.of(config));

        JsonNode result = globalUiService.getStyle();

        assertThat(result).isEqualTo(styleJson);
    }

    @Test
    @DisplayName("getStyle returns null when no config is present")
    void getStyleReturnsNullWhenNotPresent() {
        when(repository.findByFacilityIdAndTypeAndIsActiveTrue("GLOBAL", "NAVBAR_STYLE"))
                .thenReturn(Optional.empty());

        assertThat(globalUiService.getStyle()).isNull();
    }

    // ── saveComponents ───────────────────────────────────────────────────

    @Test
    @DisplayName("saveComponents scopes the config by the given facilityId, not the GLOBAL constant")
    void saveComponentsScopesByFacilityId() {
        when(repository.findByFacilityIdAndTypeAndIsActiveTrue("FACILITY_1", "NAVBAR_COMPONENTS"))
                .thenReturn(Optional.empty());

        ObjectNode componentsJson = mapper.createObjectNode().put("layout", "grid");
        globalUiService.saveComponents("FACILITY_1", componentsJson);

        ArgumentCaptor<GlobalUiConfig> captor = ArgumentCaptor.forClass(GlobalUiConfig.class);
        verify(repository).save(captor.capture());

        assertThat(captor.getValue().getFacilityId()).isEqualTo("FACILITY_1");
        assertThat(captor.getValue().getType()).isEqualTo("NAVBAR_COMPONENTS");
        assertThat(mapper.readTree(captor.getValue().getConfigJson())).isEqualTo(componentsJson);
    }

    // ── getComponents ────────────────────────────────────────────────────

    @Test
    @DisplayName("getComponents returns the parsed JSON for the given facility when present")
    void getComponentsReturnsParsedJsonForFacility() {
        ObjectNode componentsJson = mapper.createObjectNode().put("layout", "grid");
        GlobalUiConfig config = GlobalUiConfig.builder()
                .facilityId("FACILITY_1").type("NAVBAR_COMPONENTS")
                .configJson(mapper.writeValueAsString(componentsJson))
                .isActive(true).build();
        when(repository.findByFacilityIdAndTypeAndIsActiveTrue("FACILITY_1", "NAVBAR_COMPONENTS"))
                .thenReturn(Optional.of(config));

        JsonNode result = globalUiService.getComponents("FACILITY_1");

        assertThat(result).isEqualTo(componentsJson);
    }

    @Test
    @DisplayName("getComponents returns null when no config exists for the facility")
    void getComponentsReturnsNullWhenNotPresent() {
        when(repository.findByFacilityIdAndTypeAndIsActiveTrue("FACILITY_2", "NAVBAR_COMPONENTS"))
                .thenReturn(Optional.empty());

        assertThat(globalUiService.getComponents("FACILITY_2")).isNull();
    }
}