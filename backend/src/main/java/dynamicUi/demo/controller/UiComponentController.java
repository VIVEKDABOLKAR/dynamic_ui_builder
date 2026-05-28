package dynamicUi.demo.controller;

import dynamicUi.demo.dto.UIComponentDTO;
import dynamicUi.demo.service.UiComponentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/components")
@CrossOrigin("*")
public class UiComponentController {

    private final UiComponentService uiComponentService;

    public UiComponentController(UiComponentService uiComponentService) {
        this.uiComponentService = uiComponentService;
    }

    @PostMapping
    public UIComponentDTO createComponent(
            @RequestBody UIComponentDTO dto
    ) {
        return uiComponentService.createComponent(dto);
    }

    @GetMapping("/page/{pageCode}")
    public List<UIComponentDTO> getComponentsByPage(
            @PathVariable String pageCode
    ) {
        return uiComponentService.getComponentsByPage(pageCode);
    }

    @GetMapping("/{id}")
    public UIComponentDTO getComponentById(
            @PathVariable Long id
    ) {
        return uiComponentService.getComponentById(id);
    }

    @PutMapping("/{id}")
    public UIComponentDTO updateComponent(
            @PathVariable Long id,
            @RequestBody UIComponentDTO dto
    ) {
        return uiComponentService.updateComponent(id, dto);
    }

    @DeleteMapping("/{id}")
    public String deleteComponent(
            @PathVariable Long id
    ) {

        uiComponentService.deleteComponent(id);

        return "Component deleted successfully";
    }
}
