package dynamicUi.demo.controller;

import dynamicUi.demo.dto.UILookupDTO;
import dynamicUi.demo.service.inter.UILookupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/ui/lookups")
public class UILookupController {

    private final UILookupService uiLookupService;

    public UILookupController(UILookupService uiLookupService) {
        this.uiLookupService = uiLookupService;
    }

    @GetMapping("/master/{lookupMasterId}")
    public List<UILookupDTO> getLookupValuesByMaster(@PathVariable Long lookupMasterId) {
        return uiLookupService.getLookupsByMaster(lookupMasterId);
    }

    @GetMapping("/component/{componentId}")
    public List<UILookupDTO> getLookupValuesByComponent(@PathVariable Long componentId) {
        return uiLookupService.getLookupsByComponent(componentId);
    }

}
