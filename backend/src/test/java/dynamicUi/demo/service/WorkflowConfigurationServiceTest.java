package dynamicUi.demo.service;

import dynamicUi.demo.constant.FacilityId;
import dynamicUi.demo.dto.WorkflowConfigurationDTO;
import dynamicUi.demo.dto.WorkflowConfigurationRequest;
import dynamicUi.demo.entity.WorkflowConfiguration;
import dynamicUi.demo.entity.WorkflowStep;
import dynamicUi.demo.entity.WorkflowStepType;
import dynamicUi.demo.repoistory.WorkflowConfigurationRepository;
import dynamicUi.demo.repoistory.WorkflowStepRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class WorkflowConfigurationServiceTest {

    @Mock
    private WorkflowConfigurationRepository workflowConfigurationRepository;

    @Mock
    private WorkflowStepRepository workflowStepRepository;

    @InjectMocks
    private WorkflowConfigurationService workflowConfigurationService;

    private WorkflowStep gateCheckInStep;
    private WorkflowStep inspectionStep;
    private WorkflowStep loadingStep;

    private WorkflowConfiguration globalGateCheckIn;
    private WorkflowConfiguration globalInspection;
    private WorkflowConfiguration facilityGateCheckIn;

    //inject workflow step test data
    @BeforeEach
    void setUp() {

        gateCheckInStep = new WorkflowStep(
                1L,
                WorkflowStepType.GATE_CHECK_IN.name(),
                "Gate_check_in",
                "this is gate check in"
        );

        inspectionStep = new WorkflowStep(
                2L,
                WorkflowStepType.TRUCK_INSPECTION.name(),
                "Inspection",
                "this is inspection"
        );

        loadingStep = new WorkflowStep(
                3L,
                WorkflowStepType.LOADING.name(),
                "Loading",
                "this is loading"
        );

        globalGateCheckIn = new WorkflowConfiguration(
                1L,
                gateCheckInStep,
                FacilityId.GLOBAL.name(),
                1,
                true
        );

        globalInspection = new WorkflowConfiguration(
                2L,
                inspectionStep,
                FacilityId.GLOBAL.name(),
                2,
                true
        );

        facilityGateCheckIn = new WorkflowConfiguration(
                3L,
                gateCheckInStep,
                "FACILITY_1",
                1,
                true
        );
    }


    //find all - method
    @Test
    @DisplayName("find all returns all ordered by sequence")
    void findAllReturnsAllOrderedBySequence() {

        when(workflowConfigurationRepository.findAllByOrderBySequenceAsc())
                .thenReturn(List.of(
                        globalGateCheckIn,
                        globalInspection
                ));

        //action
        List<WorkflowConfigurationDTO> workflowConfigurationListDto = workflowConfigurationService.findAll();

        //assert check
        assertThat(workflowConfigurationListDto).hasSize(2);

        assertThat(workflowConfigurationListDto.get(0))
                .usingRecursiveComparison()
                .isEqualTo(
                        new WorkflowConfigurationDTO(
                                1L,
                                1L,
                                WorkflowStepType.GATE_CHECK_IN.name(),
                                "Gate_check_in",
                                1,
                                true,
                                FacilityId.GLOBAL.name()
                        )
                );

        assertThat(workflowConfigurationListDto.get(1))
                .usingRecursiveComparison()
                .isEqualTo(
                        new WorkflowConfigurationDTO(
                                2L,
                                2L,
                                WorkflowStepType.TRUCK_INSPECTION.name(),
                                "Inspection",
                                2,
                                true,
                                FacilityId.GLOBAL.name()
                        )
                );

        verify(workflowConfigurationRepository).findAllByOrderBySequenceAsc();
    }

    // ── findForFacilityEffictive ─────────────────────────────────────────

    @Test
    @DisplayName("find for facility effective returns all for global facility ID")
    void findForFacilityEffictiveReturnsAllForGlobalFacilityId() {
        when(workflowConfigurationRepository.findAllByOrderBySequenceAsc())
                .thenReturn(List.of(globalGateCheckIn, globalInspection));

        List<WorkflowConfigurationDTO> result =
                workflowConfigurationService.findForFacilityEffictive(FacilityId.GLOBAL.name());

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getWorkflowStepCode()).isEqualTo(WorkflowStepType.GATE_CHECK_IN.name());

        // GLOBAL should route straight to findAll() — never hit the
        // facility-scoped query at all.
        verify(workflowConfigurationRepository, never()).findByFacilityIdOrderBySequenceAsc(any());
    }

    @Test
    @DisplayName("find for facility effective falls back to global when facility has no rows")
    void findForFacilityEffictiveFallsBackToGlobalWhenFacilityHasNoRows() {
        when(workflowConfigurationRepository.findByFacilityIdOrderBySequenceAsc("FACILITY_2"))
                .thenReturn(List.of());
        when(workflowConfigurationRepository.findAllByOrderBySequenceAsc())
                .thenReturn(List.of(globalGateCheckIn, globalInspection));

        List<WorkflowConfigurationDTO> result =
                workflowConfigurationService.findForFacilityEffictive("FACILITY_2");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(WorkflowConfigurationDTO::getFacilityId)
                .containsOnly(FacilityId.GLOBAL.name());

        verify(workflowConfigurationRepository).findByFacilityIdOrderBySequenceAsc("FACILITY_2");
        verify(workflowConfigurationRepository).findAllByOrderBySequenceAsc();
    }

    @Test
    @DisplayName("find for facility effective returns facility rows when present")
    void findForFacilityEffictiveReturnsFacilityRowsWhenPresent() {
        when(workflowConfigurationRepository.findByFacilityIdOrderBySequenceAsc("FACILITY_1"))
                .thenReturn(List.of(facilityGateCheckIn));

        List<WorkflowConfigurationDTO> result =
                workflowConfigurationService.findForFacilityEffictive("FACILITY_1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFacilityId()).isEqualTo("FACILITY_1");

        // facility rows exist, so it must never fall back to findAll()
        verify(workflowConfigurationRepository, never()).findAllByOrderBySequenceAsc();
    }

    // ── findForFacility ──────────────────────────────────────────────────

    @Test
    @DisplayName("find for facility returns only raw rows with no fallback")
    void findForFacilityReturnsOnlyRawRowsNoFallback() {
        when(workflowConfigurationRepository.findByFacilityIdOrderBySequenceAsc("FACILITY_2"))
                .thenReturn(List.of());

        List<WorkflowConfigurationDTO> result =
                workflowConfigurationService.findForFacility("FACILITY_2");

        assertThat(result).isEmpty();
        // unlike findForFacilityEffictive, this one must NOT fall back
        verify(workflowConfigurationRepository, never()).findAllByOrderBySequenceAsc();
    }

    // ── create ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("create throws not found when step is missing")
    void createThrowsNotFoundWhenStepMissing() {
        WorkflowConfigurationRequest request = WorkflowConfigurationRequest.builder()
                .workflowStepId(99L)
                .sequence(1)
                .active(true)
                .build();

        when(workflowStepRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workflowConfigurationService.create(request, "FACILITY_1"))
                .hasMessageContaining("Workflow step not found: 99");

        verify(workflowConfigurationRepository, never()).save(any());
    }

    @Test
    @DisplayName("create throws conflict when step already exists in config for facility")
    void createThrowsConflictWhenStepAlreadyInConfigForFacility() {
        WorkflowConfigurationRequest request = WorkflowConfigurationRequest.builder()
                .workflowStepId(1L)
                .sequence(1)
                .active(true)
                .build();

        when(workflowStepRepository.findById(1L)).thenReturn(Optional.of(gateCheckInStep));
        when(workflowConfigurationRepository.existsByWorkflowStep_IdAndFacilityId(1L, "FACILITY_1"))
                .thenReturn(true);

        assertThatThrownBy(() -> workflowConfigurationService.create(request, "FACILITY_1"))
                .hasMessageContaining("Gate_check_in")
                .hasMessageContaining("already part of the workflow configuration");

        verify(workflowConfigurationRepository, never()).save(any());
    }

    @Test
    @DisplayName("create auto increments sequence per facility when not provided")
    void createAutoIncrementsSequencePerFacilityWhenNotProvided() {
        WorkflowConfigurationRequest request = WorkflowConfigurationRequest.builder()
                .workflowStepId(3L)
                .sequence(null) // not provided — service must compute it
                .active(true)
                .build();

        // facility already has sequences 1 and 2 configured
        WorkflowConfiguration facilityInspection = new WorkflowConfiguration(
                4L, inspectionStep, "FACILITY_1", 2, true);

        when(workflowStepRepository.findById(3L)).thenReturn(Optional.of(loadingStep));
        when(workflowConfigurationRepository.existsByWorkflowStep_IdAndFacilityId(3L, "FACILITY_1"))
                .thenReturn(false);
        when(workflowConfigurationRepository.findByFacilityIdOrderBySequenceAsc("FACILITY_1"))
                .thenReturn(List.of(facilityGateCheckIn, facilityInspection));
        when(workflowConfigurationRepository.save(any())).thenAnswer(inv -> {
            WorkflowConfiguration saved = inv.getArgument(0);
            saved.setId(50L);
            return saved;
        });

        WorkflowConfigurationDTO result = workflowConfigurationService.create(request, "FACILITY_1");

        assertThat(result.getSequence()).isEqualTo(3); // max(1,2) + 1

        ArgumentCaptor<WorkflowConfiguration> captor = ArgumentCaptor.forClass(WorkflowConfiguration.class);
        verify(workflowConfigurationRepository).save(captor.capture());
        assertThat(captor.getValue().getSequence()).isEqualTo(3);
    }

    @Test
    @DisplayName("create uses provided sequence when given")
    void createUsesProvidedSequenceWhenGiven() {
        WorkflowConfigurationRequest request = WorkflowConfigurationRequest.builder()
                .workflowStepId(3L)
                .sequence(5) // explicitly provided
                .active(true)
                .build();

        when(workflowStepRepository.findById(3L)).thenReturn(Optional.of(loadingStep));
        when(workflowConfigurationRepository.existsByWorkflowStep_IdAndFacilityId(3L, "FACILITY_1"))
                .thenReturn(false);
        when(workflowConfigurationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        WorkflowConfigurationDTO result = workflowConfigurationService.create(request, "FACILITY_1");

        assertThat(result.getSequence()).isEqualTo(5);
        // sequence was provided, so the auto-increment lookup must be skipped
        verify(workflowConfigurationRepository, never()).findByFacilityIdOrderBySequenceAsc(any());
    }

    // ── update ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("update changes sequence")
    void updateChangesSequence() {
        globalGateCheckIn.setActive(false);
        WorkflowConfigurationRequest request = WorkflowConfigurationRequest.builder()
                .sequence(7)
                .active(true) // unchanged — no deactivation guard triggered
                .build();

        when(workflowConfigurationRepository.findById(1L)).thenReturn(Optional.of(globalGateCheckIn));
        when(workflowConfigurationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        WorkflowConfigurationDTO result = workflowConfigurationService.update(1L, request);

        assertThat(result.getSequence()).isEqualTo(7);
    }

    @Test
    @DisplayName("update throws conflict when deactivating last active step")
    void updateThrowsConflictWhenDeactivatingLastActiveStep() {
        WorkflowConfigurationRequest request = WorkflowConfigurationRequest.builder()
                .active(false) // trying to turn off the only active step
                .build();

        when(workflowConfigurationRepository.findById(1L)).thenReturn(Optional.of(globalGateCheckIn));
        // only this row is active — excluding it leaves zero
        when(workflowConfigurationRepository.findByActiveTrueOrderBySequenceAsc())
                .thenReturn(List.of(globalGateCheckIn));

        assertThatThrownBy(() -> workflowConfigurationService.update(1L, request))
                .hasMessageContaining("At least one workflow step must remain active.");

        verify(workflowConfigurationRepository, never()).save(any());
    }

    @Test
    @DisplayName("update allows deactivating when other active steps remain")
    void updateAllowsDeactivatingWhenOtherActiveStepsRemain() {
        WorkflowConfigurationRequest request = WorkflowConfigurationRequest.builder()
                .active(false)
                .build();

        when(workflowConfigurationRepository.findById(1L)).thenReturn(Optional.of(globalGateCheckIn));
        // another active row (id 2) besides the one being updated (id 1)
        when(workflowConfigurationRepository.findByActiveTrueOrderBySequenceAsc())
                .thenReturn(List.of(globalGateCheckIn, globalInspection));
        when(workflowConfigurationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        WorkflowConfigurationDTO result = workflowConfigurationService.update(1L, request);

        assertThat(result.isActive()).isFalse();
        verify(workflowConfigurationRepository).save(any());
    }

    @Test
    @DisplayName("update throws not found when missing")
    void updateThrowsNotFoundWhenMissing() {
        when(workflowConfigurationRepository.findById(99L)).thenReturn(Optional.empty());

        WorkflowConfigurationRequest request = WorkflowConfigurationRequest.builder().active(true).build();

        assertThatThrownBy(() -> workflowConfigurationService.update(99L, request))
                .hasMessageContaining("Workflow configuration not found: 99");

        verify(workflowConfigurationRepository, never()).save(any());
    }

    // ── delete ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("delete Workflow configuration")
    void deleteWorkflowConfiguration() {
        globalGateCheckIn.setActive(false);
        when(workflowConfigurationRepository.findById(1L)).thenReturn(Optional.of(globalGateCheckIn));

        // Act
        workflowConfigurationService.delete(1L);

        // Assert
        verify(workflowConfigurationRepository)
                .deleteById(1L);

    }

    @Test
    @DisplayName("delete throws conflict when deactivating last active step")
    void deleteThrowsConflictWhenDeactivatingLastActiveStep() {
        when(workflowConfigurationRepository.findById(1L)).thenReturn(Optional.of(globalGateCheckIn));
        when(workflowConfigurationRepository.findByActiveTrueOrderBySequenceAsc())
                .thenReturn(List.of(globalGateCheckIn));

        assertThatThrownBy(() -> workflowConfigurationService.delete(1L))
                .hasMessageContaining("At least one workflow step must remain active");

        verify(workflowConfigurationRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("delete removes when other active steps remain")
    void deleteRemovesWhenOtherActiveStepsRemain() {
        when(workflowConfigurationRepository.findById(1L)).thenReturn(Optional.of(globalGateCheckIn));
        when(workflowConfigurationRepository.findByActiveTrueOrderBySequenceAsc())
                .thenReturn(List.of(globalGateCheckIn, globalInspection));

        workflowConfigurationService.delete(1L);

        verify(workflowConfigurationRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete throws not found when missing")
    void deleteThrowsNotFoundWhenMissing() {
        when(workflowConfigurationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workflowConfigurationService.delete(99L))
                .hasMessageContaining("Workflow configuration not found: 99");

        verify(workflowConfigurationRepository, never()).deleteById(any());
    }

    // ── getActiveStepsOrdered ────────────────────────────────────────────

    @Test
    @DisplayName("get active steps ordered returns ordered step types")
    void getActiveStepsOrderedReturnsOrderedStepTypes() {
        when(workflowConfigurationRepository.findByFacilityIdAndActiveTrueOrderBySequenceAsc("FACILITY_1"))
                .thenReturn(List.of(facilityGateCheckIn));

        List<WorkflowStepType> result = workflowConfigurationService.getActiveStepsOrdered("FACILITY_1");

        assertThat(result).containsExactly(WorkflowStepType.GATE_CHECK_IN);
    }

    @Test
    @DisplayName("get active steps ordered throws conflict when none configured")
    void getActiveStepsOrderedThrowsConflictWhenNoneConfigured() {
        when(workflowConfigurationRepository.findByFacilityIdAndActiveTrueOrderBySequenceAsc("FACILITY_1"))
                .thenReturn(List.of());

        assertThatThrownBy(() -> workflowConfigurationService.getActiveStepsOrdered("FACILITY_1"))
                .hasMessageContaining("No active workflow steps are configured");
    }

    @Test
    @DisplayName("get active steps ordered throws server error for unknown step code")
    void getActiveStepsOrderedThrowsServerErrorForUnknownStepCode() {
        WorkflowStep unknownStep = new WorkflowStep(9L, "UNKNOWN_CODE", "Unknown", "n/a");
        WorkflowConfiguration badConfig = new WorkflowConfiguration(9L, unknownStep, "FACILITY_1", 1, true);

        when(workflowConfigurationRepository.findByFacilityIdAndActiveTrueOrderBySequenceAsc("FACILITY_1"))
                .thenReturn(List.of(badConfig));

        assertThatThrownBy(() -> workflowConfigurationService.getActiveStepsOrdered("FACILITY_1"))
                .hasMessageContaining("unknown step code 'UNKNOWN_CODE'");
    }

    // ── getActiveEffectiveStepsOrdered ───────────────────────────────────

    @Test
    @DisplayName("get active effective steps ordered falls back to global when facility is empty")
    void getActiveEffectiveStepsOrderedFallsBackToGlobalWhenFacilityEmpty() {
        when(workflowConfigurationRepository.findByFacilityIdAndActiveTrueOrderBySequenceAsc("FACILITY_2"))
                .thenReturn(List.of());
        when(workflowConfigurationRepository.findByFacilityIdAndActiveTrueOrderBySequenceAsc(FacilityId.GLOBAL.name()))
                .thenReturn(List.of(globalGateCheckIn, globalInspection));

        List<WorkflowStepType> result = workflowConfigurationService.getActiveEffectiveStepsOrdered("FACILITY_2");

        assertThat(result).containsExactly(WorkflowStepType.GATE_CHECK_IN, WorkflowStepType.TRUCK_INSPECTION);
        verify(workflowConfigurationRepository)
                .findByFacilityIdAndActiveTrueOrderBySequenceAsc(FacilityId.GLOBAL.name());
    }

    @Test
    @DisplayName("get active effective steps ordered throws server error when global step code is unknown")
    void getActiveEffectiveStepsOrderedThrowsServerErrorWhenGlobalStepCodeIsUnknown() {
        when(workflowConfigurationRepository.findByFacilityIdAndActiveTrueOrderBySequenceAsc("FACILITY_2"))
                .thenReturn(List.of());
        when(workflowConfigurationRepository.findByFacilityIdAndActiveTrueOrderBySequenceAsc(FacilityId.GLOBAL.name()))
                .thenReturn(List.of(
                        new WorkflowConfiguration(1L, new WorkflowStep(1L, "FOO", "foo", "foo"), FacilityId.GLOBAL.name(), 1, true)
                ));

        assertThatThrownBy(() -> workflowConfigurationService.getActiveEffectiveStepsOrdered("FACILITY_2"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Workflow configuration references unknown step code '" + "FOO" + "'.");

        verify(workflowConfigurationRepository)
                .findByFacilityIdAndActiveTrueOrderBySequenceAsc(FacilityId.GLOBAL.name());
    }

    @Test
    @DisplayName("get active effective steps ordered returns facility steps when present")
    void getActiveEffectiveStepsOrderedReturnsFacilityStepsWhenPresent() {
        when(workflowConfigurationRepository.findByFacilityIdAndActiveTrueOrderBySequenceAsc("FACILITY_1"))
                .thenReturn(List.of(facilityGateCheckIn));

        List<WorkflowStepType> result = workflowConfigurationService.getActiveEffectiveStepsOrdered("FACILITY_1");

        assertThat(result).containsExactly(WorkflowStepType.GATE_CHECK_IN);
        // facility had rows, so it must never fall back to the GLOBAL query
        verify(workflowConfigurationRepository, never())
                .findByFacilityIdAndActiveTrueOrderBySequenceAsc(FacilityId.GLOBAL.name());
    }

    @Test
    @DisplayName("get active effective steps ordered throws server error for unknown step code")
    void getActiveEffectiveStepsOrderedThrowsServerErrorForUnknownStepCode() {
        when(workflowConfigurationRepository.findByFacilityIdAndActiveTrueOrderBySequenceAsc("FACILITY_1"))
                .thenReturn(List.of(
                        new WorkflowConfiguration(1L, new WorkflowStep(1L, "FOO", "foo", "foo"), "FACILITY_1", 1, true)
                ));

        //action -assert throws
        assertThatThrownBy(() -> workflowConfigurationService.getActiveEffectiveStepsOrdered("FACILITY_1"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Workflow configuration references unknown step code '" + "FOO" + "'.");

        verify(workflowConfigurationRepository, never())
                .findByFacilityIdAndActiveTrueOrderBySequenceAsc(FacilityId.GLOBAL.name());
    }


}