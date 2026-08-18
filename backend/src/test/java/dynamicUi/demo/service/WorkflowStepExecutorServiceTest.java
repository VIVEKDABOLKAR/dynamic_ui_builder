package dynamicUi.demo.service;

import dynamicUi.demo.constant.FacilityId;
import dynamicUi.demo.entity.*;
import dynamicUi.demo.repoistory.GateCheckInRepository;
import dynamicUi.demo.repoistory.JobOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkflowStepExecutorServiceTest {

    @Mock
    private JobOrderRepository jobOrderRepository;

    @Mock
    private JobStepService jobStepService;

    @Mock
    private JpaRepository<GateCheckIn, Long> gateCheckInLongJpaRepository;

    @InjectMocks
    private WorkflowStepExecutorService workflowStepExecutorService;

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
    @DisplayName("execute saves entity and completes step when job order at expected step")
    void executeSavesEntityAndCompletesStepWhenJobOrderAtExpectedStep() {
        JobOrder jobOrder = JobOrder.builder()
                .id(1L)
                .currentStep(WorkflowStepType.GATE_CHECK_IN)
                .appointmentTime(LocalDateTime.now())
                .status(JobOrderStatus.IN_PROGRESS)
                .build();
        GateCheckIn entity = new GateCheckIn();

        when(jobOrderRepository.findById(1L)).thenReturn(
                Optional.ofNullable(jobOrder)
        );
        when(gateCheckInLongJpaRepository.save(entity)).thenReturn(entity);

        //action
        GateCheckIn resultgateCheckIn = workflowStepExecutorService.execute(
                1L,
                WorkflowStepType.GATE_CHECK_IN,
                gateCheckInLongJpaRepository,
                entity,
                GateCheckIn::setJobOrder
        );

        // Assert
        assertThat(resultgateCheckIn).isSameAs(entity);
        assertThat(resultgateCheckIn.getJobOrder()).isEqualTo(jobOrder);
        verify(gateCheckInLongJpaRepository).save(entity);
        verify(jobStepService).completeStep(1L, WorkflowStepType.GATE_CHECK_IN);

    }

    @Test
    @DisplayName("execute throws when job order not found")
    void executeThrowsWhenJobOrderNotFound() {
        JobOrder jobOrder = JobOrder.builder()
                .id(1L)
                .currentStep(WorkflowStepType.DOCK_ASSIGNMENT)
                .appointmentTime(LocalDateTime.now())
                .status(JobOrderStatus.IN_PROGRESS)
                .build();
        GateCheckIn entity = new GateCheckIn();

        when(jobOrderRepository.findById(1L)).thenReturn(
                Optional.ofNullable(jobOrder)
        );

        //action
        assertThatThrownBy(() -> workflowStepExecutorService.execute(
                1L,
                WorkflowStepType.GATE_CHECK_IN,
                gateCheckInLongJpaRepository,
                entity,
                GateCheckIn::setJobOrder
        ))
                .isInstanceOf(IllegalStateException.class)
                        .hasMessageContaining("Job Order is not step at"+WorkflowStepType.GATE_CHECK_IN+".Current step at"+jobOrder.getCurrentStep());

        // verify
        verify(gateCheckInLongJpaRepository, never()).save(entity);
        verify(jobStepService, never()).completeStep(any(), any());

    }

    @Test
    @DisplayName("execute throws IllegalStateException when current step does not match")
    void executeThrowsIllegalStateWhenCurrentStepDoesNotMatch() {
        JobOrder jobOrder = JobOrder.builder()
                .id(1L)
                .currentStep(WorkflowStepType.DOCK_ASSIGNMENT)
                .appointmentTime(LocalDateTime.now())
                .status(JobOrderStatus.IN_PROGRESS)
                .build();
        GateCheckIn entity = new GateCheckIn();

        when(jobOrderRepository.findById(1L)).thenReturn(
                Optional.empty()
        );

        //action
        assertThatThrownBy(() -> workflowStepExecutorService.execute(
                1L,
                WorkflowStepType.GATE_CHECK_IN,
                gateCheckInLongJpaRepository,
                entity,
                GateCheckIn::setJobOrder
        ))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Job order Not Found"+ 1L);

        // verify
        verify(gateCheckInLongJpaRepository, never()).save(entity);
        verify(jobStepService, never()).completeStep(any(), any());
    }

}
