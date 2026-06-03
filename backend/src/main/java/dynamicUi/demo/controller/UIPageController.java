package dynamicUi.demo.controller;

import dynamicUi.demo.entity.UIPage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import dynamicUi.demo.service.inter.UIPageService;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/admin/pages")
public class UIPageController {

    private final UIPageService uiPageService;

    public UIPageController(UIPageService uiPageService) {
        this.uiPageService = uiPageService;
    }

    @PostMapping
    public ResponseEntity<UIPage> createPage(@RequestBody UIPage uiPage) {
        return ResponseEntity.ok(uiPageService.createPage(uiPage));
    }

    @GetMapping
    public ResponseEntity<List<UIPage>> getAllPages() {
        return ResponseEntity.ok(uiPageService.getAllPages());
    }

    @GetMapping("/{pageCode}")
    public UIPage getPageByCode(
            @PathVariable String pageCode
    ) {
        return uiPageService.getPageByCode(pageCode);
    }

    @PutMapping("/{pageCode}")
    public UIPage updatePage(
            @PathVariable String pageCode,
            @RequestBody UIPage uIPage
    ) {
        return uiPageService.updatePage(pageCode, uIPage);
    }

    @DeleteMapping("/{pageCode}")
    public String deletePage(@PathVariable String pageCode) {
        uiPageService.deletePage(pageCode);
        return "Page deactivated successfully";
    }
}
