package dynamicUi.demo.service;


import dynamicUi.demo.entity.JobOrder;
import dynamicUi.demo.entity.WorkflowStepType;
import dynamicUi.demo.repoistory.JobOrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.function.BiConsumer;

@Service
@RequiredArgsConstructor
public class WorkflowStepExecutor {

    private final JobOrderRepository jobOrderRepository;
    private final JobStepService jobStepService;

    @Transactional
    public <T> T execute(
            Long jobOrderId,
            WorkflowStepType step,
            JpaRepository<T,Long> repository,
            T entity,
            BiConsumer<T, JobOrder> setJobOrder
    ){

        JobOrder jobOrder = jobOrderRepository.findById(jobOrderId)
                                .orElseThrow(()->new RuntimeException("Job order Not Found"+ jobOrderId));

        if(jobOrder.getCurrentStep()!=step){
            throw new IllegalStateException("Job Order is not step at"+step+".Current step at"+jobOrder.getCurrentStep());
        }

        setJobOrder.accept(entity,jobOrder);
        T saved = repository.save(entity);
        jobStepService.completeStep(jobOrderId,step);
        return  saved;
    }
}
