package dynamicUi.demo.service;

import dynamicUi.demo.entity.WorkflowStep;
import dynamicUi.demo.entity.WorkflowStepType;
import dynamicUi.demo.repoistory.WorkflowConfigurationRepository;
import dynamicUi.demo.repoistory.WorkflowStepRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
public class WorkflowStepServiceTest {

    @Mock
    private WorkflowStepRepository workflowStepRepository;

    @Mock
    private WorkflowConfigurationRepository workflowConfigurationRepository;

    @InjectMocks
    private WorkflowStepService workflowStepService;

    // ── create ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("Creating workflow step by passing valid code and name")
    void createSavesWhenCodeAndNameValid() {
        // Build WorkflowStep
        WorkflowStep workflowStep = WorkflowStep.builder()
                .code("gate_check_in")   // lowercase on purpose — service should uppercase it
                .name("  Gate Check In  ") // padded on purpose — service should trim it
                .build();

        //build mock object implementation
        when(workflowStepRepository.existsByCode("GATE_CHECK_IN")).thenReturn(false);
        when(workflowStepRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        //action
        WorkflowStep resultWorkflow = workflowStepService.create(workflowStep);

        //Assert
        assertThat(resultWorkflow.getCode()).isEqualTo("GATE_CHECK_IN");
        assertThat(resultWorkflow.getName()).isEqualTo("Gate Check In");

        //verify repo called
        verify(workflowStepRepository).save(workflowStep);
    }

    @Test
    @DisplayName("throws bad request when code is missing or null")
    void createThrowsBadRequestWhenCodeMissing() {
        //build missing code workflow step
        WorkflowStep workflowStep_CodeNull = WorkflowStep.builder()
                .code(null)
                .name("Gate ")
                .build();

        WorkflowStep workflowStep_CodeEmpty = WorkflowStep.builder()
                .code(" ")
                .name("Gate ")
                .build();

        //action
        assertThatThrownBy(() -> workflowStepService.create(workflowStep_CodeNull))//catch if any throwable error occurs
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Step code is required.");

        assertThatThrownBy(() -> workflowStepService.create(workflowStep_CodeEmpty))//catch if any throwable error occurs
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Step code is required.");

        //verify
        verify(workflowStepRepository, never()).save(any());
    }

    @Test
    @DisplayName("throws bad request when name is missing")
    void createThrowsBadRequestWhenNameMissing() {
        //build object
        WorkflowStep workflowStep_NameEmpty = WorkflowStep.builder()
                .code("Gate_check_in")
                .name(" ")
                .build();

        WorkflowStep workflowStep_NameNull = WorkflowStep.builder()
                .code("Gate_check_in")
                .name(null)
                .build();


        //action to catch error
        assertThatThrownBy(() -> workflowStepService.create(workflowStep_NameEmpty))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Step name is required.");

        assertThatThrownBy(() -> workflowStepService.create(workflowStep_NameNull))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Step name is required.");

        //verfiy repo interaction is never
        verify(workflowStepRepository, never()).save(any());
    }

    @Test
    @DisplayName("creating by normalizes code to uppercase")
    void createNormalizesCodeToUppercase() {
        //build workflow step
        WorkflowStep workflowStep = WorkflowStep.builder()
                .code("gate_check_in")
                .name("Gate_chek_in")
                .build();

        //build mock object implementing
        when(workflowStepRepository.existsByCode("GATE_CHECK_IN")).thenReturn(false);
        when(workflowStepRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        //call action
        WorkflowStep resultWorkflowStep = workflowStepService.create(workflowStep);

        //verify
        assertThat(resultWorkflowStep.getCode()).isEqualTo(WorkflowStepType.GATE_CHECK_IN.name());
        verify(workflowStepRepository).save(workflowStep);
    }

    @Test
    @DisplayName("Throws bad request for unknown step type")
    void createThrowsBadRequestForUnknownStepType() {
        //build unknown WorkFlowStep
        WorkflowStep workflowStep = WorkflowStep.builder()
                .code("foo") //unknown workflow step type
                .name("Gate_Check_In")
                .build();

        //action
        // NOTE: the service wraps the invalid code in single quotes in the
        // message ("Unknown step code 'FOO'. Must be one of: ..."), so the
        // assertion must include the quotes to actually match.
        assertThatThrownBy(() -> workflowStepService.create(workflowStep))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Unknown step code :- FOO");

        //verify — validation fails before any repository call is made
        verifyNoInteractions(workflowStepRepository);
    }

    @Test
    @DisplayName("throws conflict when code already exists")
    void createThrowsConflictWhenCodeAlreadyExists() {
        //build workflow step with a valid, known code
        WorkflowStep workflowStep = WorkflowStep.builder()
                .code("gate_check_in")
                .name("Gate Check In")
                .build();

        //the normalized code is already taken
        when(workflowStepRepository.existsByCode("GATE_CHECK_IN")).thenReturn(true);

        //action + assert
        assertThatThrownBy(() -> workflowStepService.create(workflowStep))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("A workflow step with code 'GATE_CHECK_IN' already exists.");

        //verify — never reaches save()
        verify(workflowStepRepository, never()).save(any());
    }

    // ── update ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("update updates name and description only, code stays unchanged")
    void updateUpdatesNameAndDescriptionOnly() {
        //existing persisted step
        WorkflowStep existing = WorkflowStep.builder()
                .id(1L)
                .code("GATE_CHECK_IN")
                .name("Old Name")
                .description("Old description")
                .build();

        //incoming payload — code is ignored even if set
        WorkflowStep payload = WorkflowStep.builder()
                .code("SHOULD_BE_IGNORED")
                .name("  New Name  ")
                .description("New description")
                .build();

        when(workflowStepRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(workflowStepRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        WorkflowStep result = workflowStepService.update(1L, payload);

        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getDescription()).isEqualTo("New description");
        assertThat(result.getCode()).isEqualTo("GATE_CHECK_IN"); // unchanged
        verify(workflowStepRepository).save(existing);
    }

    @Test
    @DisplayName("update throws not found when step id does not exist")
    void updateThrowsNotFoundWhenMissing() {
        when(workflowStepRepository.findById(99L)).thenReturn(Optional.empty());

        WorkflowStep payload = WorkflowStep.builder().name("New Name").build();

        assertThatThrownBy(() -> workflowStepService.update(99L, payload))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Workflow step not found: 99");

        verify(workflowStepRepository, never()).save(any());
    }

    @Test
    @DisplayName("update throws bad request when name is blank or null")
    void updateThrowsBadRequestWhenNameBlank() {
        WorkflowStep existing = WorkflowStep.builder()
                .id(1L)
                .code("GATE_CHECK_IN")
                .name("Old Name")
                .build();

        when(workflowStepRepository.findById(1L)).thenReturn(Optional.of(existing));

        WorkflowStep payload_NameEmpty = WorkflowStep.builder().name("   ").build();
        WorkflowStep payload_NameNull = WorkflowStep.builder().name(null).build();

        assertThatThrownBy(() -> workflowStepService.update(1L, payload_NameEmpty))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Step name is required.");

        assertThatThrownBy(() -> workflowStepService.update(1L, payload_NameNull))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Step name is required.");

        verify(workflowStepRepository, never()).save(any());
    }

    @Test
    @DisplayName("update does not allow changing the code even when provided")
    void updateDoesNotAllowCodeChange() {
        WorkflowStep existing = WorkflowStep.builder()
                .id(1L).code("GATE_CHECK_IN").name("Old Name").build();
        WorkflowStep payload = WorkflowStep.builder()
                .code("TRUCK_INSPECTION") // attempted change — should be ignored
                .name("Updated Name")
                .build();

        when(workflowStepRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(workflowStepRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        WorkflowStep result = workflowStepService.update(1L, payload);

        assertThat(result.getCode()).isEqualTo("GATE_CHECK_IN");
    }

    // ── delete ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("delete removes step when it exists and is unused")
    void deleteRemovesUnusedStep() {
        when(workflowStepRepository.existsById(1L)).thenReturn(true);
        when(workflowConfigurationRepository.existsByWorkflowStep_Id(1L)).thenReturn(false);

        workflowStepService.delete(1L);

        verify(workflowStepRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete throws not found when step id does not exist")
    void deleteThrowsNotFoundWhenMissing() {
        when(workflowStepRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> workflowStepService.delete(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Workflow step not found: 99");

        verify(workflowStepRepository, never()).deleteById(any());
        verifyNoInteractions(workflowConfigurationRepository);
    }

    @Test
    @DisplayName("delete throws conflict when step is referenced by a workflow configuration")
    void deleteThrowsConflictWhenReferencedByConfiguration() {
        when(workflowStepRepository.existsById(1L)).thenReturn(true);
        when(workflowConfigurationRepository.existsByWorkflowStep_Id(1L)).thenReturn(true);

        assertThatThrownBy(() -> workflowStepService.delete(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("used in the workflow configuration");

        verify(workflowStepRepository, never()).deleteById(any());
    }

    // ── findAll ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("findAll delegates to the repository")
    void findAllDelegatesToRepository() {
        List<WorkflowStep> steps = List.of(
                WorkflowStep.builder().id(1L).code("GATE_CHECK_IN").name("Gate Check In").build());
        when(workflowStepRepository.findAll()).thenReturn(steps);

        assertThat(workflowStepService.findAll()).isEqualTo(steps);
    }
}