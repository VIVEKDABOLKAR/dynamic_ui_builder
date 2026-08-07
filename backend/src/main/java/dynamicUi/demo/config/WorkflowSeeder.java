package dynamicUi.demo.config;

import dynamicUi.demo.entity.WorkflowConfiguration;
import dynamicUi.demo.entity.WorkflowStep;
import dynamicUi.demo.entity.WorkflowStepType;
import dynamicUi.demo.repoistory.WorkflowConfigurationRepository;
import dynamicUi.demo.repoistory.WorkflowStepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * One-time bootstrap: turns the old hardcoded WorkflowStepType.values()
 * order into WorkflowStep + WorkflowConfiguration rows, so behaviour is
 * identical to before this change on the very first startup after
 * upgrading. Runs only when workflow_step is empty — safe to leave in
 * place permanently, it's a no-op on every later startup.
 */
@Component
@RequiredArgsConstructor
public class WorkflowSeeder implements CommandLineRunner {

    private final WorkflowStepRepository workflowStepRepository;
    private final WorkflowConfigurationRepository workflowConfigurationRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (workflowStepRepository.count() > 0) {
            return; // already seeded (or admin has taken over managing steps)
        }

        WorkflowStepType[] defaultOrder = WorkflowStepType.values();

        for (int i = 0; i < defaultOrder.length; i++) {
            WorkflowStepType type = defaultOrder[i];

            WorkflowStep step = workflowStepRepository.save(
                    WorkflowStep.builder()
                            .code(type.name())
                            .name(toDisplayName(type.name()))
                            .build()
            );

            workflowConfigurationRepository.save(
                    WorkflowConfiguration.builder()
                            .workflowStep(step)
                            .sequence(i + 1)
                            .active(true)
                            .build()
            );
        }
    }

    private String toDisplayName(String code) {
        String[] words = code.split("_");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(word.charAt(0)).append(word.substring(1).toLowerCase());
        }
        return sb.toString();
    }
}
