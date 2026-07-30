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

    /**
     * Generic type-scoped lookups for admin dropdowns, e.g.
     * GET /api/ui/lookups/type/MODULE_CODE, /type/PARENT_MENU
     */
    @GetMapping("/type/{lookupType}")
    public List<UILookupDTO> getLookupValuesByType(@PathVariable String lookupType) {
        return uiLookupService.getLookupsByType(lookupType);
    }

    //CRUD operation
    @GetMapping("/{id}")
    public UILookupDTO getLookupById(@PathVariable Long id) {
        return uiLookupService.getLookupById(id);
    }

    @PostMapping
    public UILookupDTO createLookup(@RequestBody UILookupDTO dto) {
        return uiLookupService.createLookup(dto);
    }

    @PutMapping("/{id}")
    public UILookupDTO updateLookup(@PathVariable Long id,
                                    @RequestBody UILookupDTO dto) {
        return uiLookupService.updateLookup(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteLookup(@PathVariable Long id) {
        uiLookupService.deleteLookup(id);
    }

}
