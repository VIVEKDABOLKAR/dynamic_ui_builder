package dynamicUi.demo.service;

import dynamicUi.demo.entity.*;
import dynamicUi.demo.repoistory.JobOrderRepository;
import dynamicUi.demo.repoistory.JobStepRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JobStepServiceTest {

    @Mock
    private JobStepRepository jobStepRepository;

    @Mock
    private JobOrderRepository jobOrderRepository;

    @Mock
    private WorkflowConfigurationService workflowConfigurationService;


    @InjectMocks
    private JobStepService jobStepService;

    @Test
    @DisplayName("complete step marks current step completed and moves job order to next step")
    void completeStepMarksCurrentStepCompletedAndMovesToNextStep() {

        // Arrange
        JobOrder jobOrder = JobOrder.builder()
                .id(1L)
                .status(JobOrderStatus.IN_PROGRESS)
                .currentStep(WorkflowStepType.GATE_CHECK_IN)
                .build();

        JobStep currentStep = JobStep.builder()
                .id(1L)
                .jobOrder(jobOrder)
                .sequenceNo(1)
                .completedAt(null)
                .status(JobStepStatus.IN_PROGRESS)
                .step(WorkflowStepType.GATE_CHECK_IN)
                .build();

        JobStep nextStep = JobStep.builder()
                .id(2L)
                .jobOrder(jobOrder)
                .sequenceNo(2)
                .completedAt(null)
                .status(JobStepStatus.PENDING)
                .step(WorkflowStepType.TRUCK_INSPECTION)
                .build();

        when(jobStepRepository.findByJobOrder_IdAndStep(
                1L,
                WorkflowStepType.GATE_CHECK_IN
        )).thenReturn(Optional.of(currentStep));

        when(jobStepRepository
                .findFirstByJobOrder_IdAndSequenceNoGreaterThanOrderBySequenceNoAsc(
                        1L,
                        1
                ))
                .thenReturn(Optional.of(nextStep));

        // Act
        jobStepService.completeStep(
                1L,
                WorkflowStepType.GATE_CHECK_IN
        );

        // Assert - current step
        assertThat(currentStep.getStatus())
                .isEqualTo(JobStepStatus.COMPLETED);

        assertThat(currentStep.getCompletedAt())
                .isNotNull();

        // Assert - next step
        assertThat(nextStep.getStatus())
                .isEqualTo(JobStepStatus.IN_PROGRESS);

        // Assert - job order
        assertThat(jobOrder.getCurrentStep())
                .isEqualTo(WorkflowStepType.TRUCK_INSPECTION);

        assertThat(jobOrder.getStatus())
                .isEqualTo(JobOrderStatus.IN_PROGRESS);

        verify(jobOrderRepository)
                .save(jobOrder);
    }

    @Test
    @DisplayName("complete final step marks job order as completed")
    void completeFinalStepMarksJobOrderCompleted() {

        // Arrange
        JobOrder jobOrder = JobOrder.builder()
                .id(1L)
                .status(JobOrderStatus.IN_PROGRESS)
                .currentStep(WorkflowStepType.GATE_CHECK_IN)
                .build();

        JobStep currentStep = JobStep.builder()
                .id(1L)
                .jobOrder(jobOrder)
                .sequenceNo(1)
                .completedAt(null)
                .status(JobStepStatus.IN_PROGRESS)
                .step(WorkflowStepType.GATE_CHECK_IN)
                .build();

        when(jobStepRepository.findByJobOrder_IdAndStep(
                1L,
                WorkflowStepType.GATE_CHECK_IN
        )).thenReturn(Optional.of(currentStep));

        when(jobStepRepository
                .findFirstByJobOrder_IdAndSequenceNoGreaterThanOrderBySequenceNoAsc(
                        1L,
                        1
                ))
                .thenReturn(Optional.empty());

        // Act
        jobStepService.completeStep(
                1L,
                WorkflowStepType.GATE_CHECK_IN
        );

        // Assert - current step
        assertThat(currentStep.getStatus())
                .isEqualTo(JobStepStatus.COMPLETED);

        assertThat(currentStep.getCompletedAt())
                .isNotNull();

        // Assert - job order
        assertThat(jobOrder.getStatus())
                .isEqualTo(JobOrderStatus.COMPLETED);

        assertThat(jobOrder.getCurrentStep())
                .isNull();

        verify(jobOrderRepository)
                .save(jobOrder);
    }

    @Test
    @DisplayName("complete step throws exception when step does not exist")
    void completeStepThrowsExceptionWhenStepDoesNotExist() {

        // Arrange
        when(jobStepRepository.findByJobOrder_IdAndStep(
                1L,
                WorkflowStepType.GATE_CHECK_IN
        )).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() ->
                jobStepService.completeStep(
                        1L,
                        WorkflowStepType.GATE_CHECK_IN
                )
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Step not found: GATE_CHECK_IN");

        verify(jobOrderRepository, never())
                .save(any());
    }
}
