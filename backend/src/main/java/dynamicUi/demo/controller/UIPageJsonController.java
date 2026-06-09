package dynamicUi.demo.controller;

import dynamicUi.demo.entity.UIPage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dynamicUi.demo.entity.UIPageJson;
import dynamicUi.demo.service.inter.UIPageJsonService;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/ui/pages")
public class UIPageJsonController {

    private final UIPageJsonService uiPageJsonService;

    public UIPageJsonController(UIPageJsonService uiPageJsonService) {
        this.uiPageJsonService = uiPageJsonService;
    }

    @GetMapping("/{pageCode}")
    public ResponseEntity<UIPageJson> getPageJson(@PathVariable String pageCode) {
        return ResponseEntity.ok(uiPageJsonService.getByPageCode(pageCode));
    }

    @GetMapping
    public ResponseEntity<List<UIPage>> getAllActivePages() {
        return ResponseEntity.ok(uiPageJsonService.getAllActivePages());
    }
}