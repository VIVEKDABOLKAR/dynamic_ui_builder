package dynamicUi.demo.controller;
import dynamicUi.demo.entity.TruckInspection;
import dynamicUi.demo.service.TruckInspectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/truck-inspections")
@RequiredArgsConstructor
public class TruckInspectorController {

    private final TruckInspectionService service;

    @PostMapping
    public TruckInspection inspect(@RequestBody TruckInspectionRequest request){
       TruckInspection inspection = TruckInspection.builder()
                .brakeStatus(request.brakeStatus())
                .tyreStatus(request.tyreStatus())
                .inspectorUser(request.inspectorUser())
                .remarks(request.remarks()).build();

    return service.inspect(inspection,request.jobOrderId());

    }

    @GetMapping("/{jobOrderId}")
    public TruckInspection getByJobOrder(@PathVariable Long jobOrderId){
            return service.findByJobOrder_Id(jobOrderId).
    }

    public record TruckInspectionRequest(
            Long jobOrderId,
            String brakeStatus,
            String tyreStatus,
            String inspectorUser,
            String remarks
    ) {}

}
