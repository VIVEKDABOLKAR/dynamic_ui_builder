package dynamicUi.demo.service;

import dynamicUi.demo.constant.FacilityId;
import dynamicUi.demo.entity.*;
import dynamicUi.demo.repoistory.JobOrderRepository;
import dynamicUi.demo.repoistory.JobStepRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Year;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JobOrderServiceTest {
    @Mock
    private JobOrderRepository jobOrderRepository;

    @Mock
    private JobStepRepository jobStepRepository;

    @Mock
    private WorkflowConfigurationService workflowConfigurationService;

    @InjectMocks
    private JobOrderService jobOrderService;
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

    @Test
    @DisplayName("create builds job order, assigns first step, and snapshots all workflow steps")
    void createBuildsJobOrderAndSnapshotsWorkflowSteps() {
        // Arrange
        JobOrder input = JobOrder.builder()
                .customerName("Acme Corp")
                .truckNumber("GJ05JU9090")
                .driverName("John")
                .build();

        List<WorkflowStepType> workflow = List.of(WorkflowStepType.GATE_CHECK_IN, WorkflowStepType.GATE_CHECK_OUT);

        when(workflowConfigurationService.getActiveEffectiveStepsOrdered("FACILITY_1")).thenReturn(workflow);

        // save() is called twice: once to obtain the generated id, once
        // after the jobOrderNumber is assigned. Mock both calls to mimic
        // JPA assigning an id on first persist.
        when(jobOrderRepository.save(any())).thenAnswer(inv -> {
            JobOrder jo = inv.getArgument(0);
            if (jo.getId() == null) {
                jo.setId(7L);
            }
            return jo;
        });

        // Act
        JobOrder result = jobOrderService.create(input, "FACILITY_1");

        // Assert — job order itself
        assertThat(result.getId()).isEqualTo(7L);
        assertThat(result.getFacilityId()).isEqualTo("FACILITY_1");
        assertThat(result.getStatus()).isEqualTo(JobOrderStatus.CREATED);
        assertThat(result.getCurrentStep()).isEqualTo(WorkflowStepType.GATE_CHECK_IN);
        assertThat(result.getJobOrderNumber()).isEqualTo("JO-" + Year.now().getValue() + "-000007");

        verify(jobOrderRepository, times(2)).save(any());

        // Assert — snapshotted JobStep rows
        ArgumentCaptor<List<JobStep>> stepCaptor = ArgumentCaptor.forClass(List.class);
        verify(jobStepRepository, times(1)).saveAll(stepCaptor.capture());

        List<JobStep> savedSteps = stepCaptor.getValue();
        assertThat(savedSteps).extracting(JobStep::getStep)
                .containsExactly(WorkflowStepType.GATE_CHECK_IN, WorkflowStepType.GATE_CHECK_OUT);
        assertThat(savedSteps).extracting(JobStep::getSequenceNo)
                .containsExactly(1, 2);
        assertThat(savedSteps.get(0).getStatus()).isEqualTo(JobStepStatus.IN_PROGRESS);
        assertThat(savedSteps.get(1).getStatus()).isEqualTo(JobStepStatus.PENDING);
        assertThat(savedSteps).allMatch(js -> js.getJobOrder() == result);
    }

    @Test
    @DisplayName("create propagates the error when no active workflow is configured for the facility")
    void createThrowsWhenNoActiveWorkflowConfigured() {
        JobOrder input = JobOrder.builder().build();

        when(workflowConfigurationService.getActiveEffectiveStepsOrdered("FACILITY_1"))
                .thenThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No active workflow steps are configured"));

        assertThatThrownBy(() -> jobOrderService.create(input, "FACILITY_1"))
                .isInstanceOf(ResponseStatusException.class);

        verify(jobOrderRepository, never()).save(any());
        verify(jobStepRepository, never()).save(any());
    }

    // ── findByFacility ───────────────────────────────────────────────────

    @Test
    @DisplayName("findByFacility delegates to the repository")
    void findByFacilityDelegatesToRepository() {
        List<JobOrder> orders = List.of(JobOrder.builder().id(1L).facilityId("FACILITY_1").build());
        when(jobOrderRepository.findByFacilityId("FACILITY_1")).thenReturn(orders);

        assertThat(jobOrderService.findByFacility("FACILITY_1")).isEqualTo(orders);
    }

    // ── findByNumber ─────────────────────────────────────────────────────

    @Test
    @DisplayName("findByNumber returns the job order when found")
    void findByNumberReturnsWhenFound() {
        JobOrder order = JobOrder.builder().id(1L).jobOrderNumber("JO-2026-000007").build();
        when(jobOrderRepository.findByJobOrderNumber("JO-2026-000007")).thenReturn(Optional.of(order));

        assertThat(jobOrderService.findByNumber("JO-2026-000007")).isEqualTo(order);
    }

    @Test
    @DisplayName("findByNumber throws when not found")
    void findByNumberThrowsWhenNotFound() {
        when(jobOrderRepository.findByJobOrderNumber("JO-MISSING")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jobOrderService.findByNumber("JO-MISSING"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Job order not found: " + "JO-MISSING");
    }

    // ── getSteps ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("getSteps delegates to the repository, ordered by sequence")
    void getStepsDelegatesToRepositoryOrderedBySequence() {
        List<JobStep> steps = List.of(
                JobStep.builder().id(10L).step(WorkflowStepType.GATE_CHECK_IN).sequenceNo(1).build(),
                JobStep.builder().id(11L).step(WorkflowStepType.TRUCK_INSPECTION).sequenceNo(2).build());
        when(jobStepRepository.findByJobOrder_IdOrderBySequenceNoAsc(1L)).thenReturn(steps);

        assertThat(jobOrderService.getSteps(1L)).isEqualTo(steps);
    }

    // ── cancel ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("cancel sets status to CANCELLED and saves")
    void cancelSetsStatusCancelledAndSaves() {
        JobOrder order = JobOrder.builder().id(1L).status(JobOrderStatus.IN_PROGRESS).build();
        when(jobOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(jobOrderRepository.save(order)).thenReturn(order);

        JobOrder result = jobOrderService.cancel(1L);

        assertThat(result.getStatus()).isEqualTo(JobOrderStatus.CANCELLED);
        verify(jobOrderRepository).save(order);
    }

    @Test
    @DisplayName("cancel throws when job order is not found")
    void cancelThrowsWhenJobOrderNotFound() {
        when(jobOrderRepository.findById(99L)).thenReturn(Optional.empty());

        // NOTE: cancel() uses the no-arg .orElseThrow(), which throws a bare
        // NoSuchElementException — not the custom RuntimeException("...")
        // pattern used elsewhere in this class (e.g. findByNumber). Worth
        // flagging as an inconsistency, but testing the actual behavior here.
        assertThatThrownBy(() -> jobOrderService.cancel(99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("job order-id not found");

        verify(jobOrderRepository, never()).save(any());
    }
}
