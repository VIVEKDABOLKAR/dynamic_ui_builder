package dynamicUi.demo.controller;

import dynamicUi.demo.dto.UILookupMasterDTO;
import dynamicUi.demo.service.inter.UILookupMasterService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ui/lookup-masters")
public class UILookupMasterController {
    private final UILookupMasterService lookupMasterService;

    public UILookupMasterController(UILookupMasterService lookupMasterService) {
        this.lookupMasterService = lookupMasterService;
    }

    @GetMapping
    public List<UILookupMasterDTO> getAllLookupMasters() {
        return lookupMasterService.getAll();
    }

    @GetMapping("/{id}")
    public UILookupMasterDTO getById(@PathVariable Long id) {
        return lookupMasterService.getById(id);
    }

    @PostMapping
    public UILookupMasterDTO create(@RequestBody UILookupMasterDTO dto) {
        return lookupMasterService.create(dto);
    }

    @PutMapping("/{id}")
    public UILookupMasterDTO update(@PathVariable Long id,
                                    @RequestBody UILookupMasterDTO dto) {
        return lookupMasterService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        lookupMasterService.delete(id);
    }
}
