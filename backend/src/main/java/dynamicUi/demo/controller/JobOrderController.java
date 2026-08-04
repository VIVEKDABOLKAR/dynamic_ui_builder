package dynamicUi.demo.controller;

import dynamicUi.demo.entity.JobOrder;
import dynamicUi.demo.entity.JobStep;
import dynamicUi.demo.service.JobOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-orders")
@RequiredArgsConstructor
public class JobOrderController {

    private final JobOrderService service;

    @PostMapping
    public JobOrder create(@RequestBody JobOrder jobOrder) {
        return service.create(jobOrder);
    }

    @GetMapping
    public List<JobOrder> listByFacility(@RequestParam String facilityId) {
        return service.findByFacility(facilityId);
    }

    @GetMapping("/{jobOrderNumber}")
    public JobOrder getByNumber(@PathVariable String jobOrderNumber) {
        return service.findByNumber(jobOrderNumber);
    }

    @GetMapping("/{id}/steps")
    public List<JobStep> getSteps(@PathVariable Long id) {
        return service.getSteps(id);
    }

    @PostMapping("/{id}/cancel")
    public JobOrder cancel(@PathVariable Long id) {
        return service.cancel(id);
    }

}