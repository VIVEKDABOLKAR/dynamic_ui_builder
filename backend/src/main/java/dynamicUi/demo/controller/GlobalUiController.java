package dynamicUi.demo.controller;

import tools.jackson.databind.JsonNode;
import dynamicUi.demo.dto.GlobalUiRequest;
import dynamicUi.demo.service.GlobalUiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;@RestController
@RequestMapping("/api/global-ui/navbar")
@RequiredArgsConstructor
public class GlobalUiController {

    private final GlobalUiService service;

    @PostMapping("/style")
    public ResponseEntity<?> saveStyle(@RequestBody JsonNode style) {
        service.saveStyle(style);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/style")
    public ResponseEntity<JsonNode> getStyle() {
        JsonNode result = service.getStyle();
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }

    @PostMapping("/components/{facilityId}")
    public ResponseEntity<?> saveComponents(@PathVariable String facilityId, @RequestBody JsonNode components) {
        service.saveComponents(facilityId, components);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/components/{facilityId}")
    public ResponseEntity<JsonNode> getComponents(@PathVariable String facilityId) {
        JsonNode result = service.getComponents(facilityId);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }
}