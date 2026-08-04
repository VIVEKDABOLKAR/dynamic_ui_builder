package dynamicUi.demo.controller;

import dynamicUi.demo.entity.GateCheckIn;
import dynamicUi.demo.service.GateCheckInService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gate-checkins")
@RequiredArgsConstructor
public class GateCheckInController {

    private final GateCheckInService service;

    @PostMapping
    public GateCheckIn checkIn(@RequestBody GateCheckInRequest request) {
        GateCheckIn gateCheckIn = GateCheckIn.builder()
                .gateNumber(request.gateNumber())
                .securityUser(request.securityUser())
                .truckNumber(request.truckNumber())
                .driverName(request.driverName())
                .remarks(request.remarks())
                .build();

        return service.checkIn(request.jobOrderId(), gateCheckIn);
    }

    @GetMapping("/{jobOrderId}")
    public GateCheckIn getByJobOrder(@PathVariable Long jobOrderId) {
        return service.getByJobOrder(jobOrderId);
    }

    public record GateCheckInRequest(
            Long jobOrderId,
            String gateNumber,
            String securityUser,
            String truckNumber,
            String driverName,
            String remarks
    ) {}
}