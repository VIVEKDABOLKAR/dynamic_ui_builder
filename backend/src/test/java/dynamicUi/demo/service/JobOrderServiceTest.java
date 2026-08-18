package dynamicUi.demo.service;

import dynamicUi.demo.repoistory.JobOrderRepository;
import dynamicUi.demo.repoistory.JobStepRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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


}
