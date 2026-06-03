package dynamicUi.demo.controller;


import dynamicUi.demo.entity.UIPageAction;
import dynamicUi.demo.service.inter.UIPageActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/actions")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UIPageActionController {

    private final UIPageActionService service;

    @PostMapping("/{pageCode}")
    public UIPageAction create(@RequestBody UIPageAction uiPageAction, @PathVariable String pageCode) {
        uiPageAction.setUiPagecode(pageCode);
        return service.create(pageCode , uiPageAction);
    }

//    @PutMapping("/{id}")
//    public UIPageAction update(
//            @PathVariable Long id,
//            @RequestBody UIPageAction uiPageAction) {
//        return service.update(id, uiPageAction);
//    }
//
//    @GetMapping("/{id}")
//    public UIPageAction getById(@PathVariable Long id) {
//        return service.getById(id);
//    }
//
//    @GetMapping
//    public List<UIPageAction> getAll() {
//        return service.getAll();
//    }
//
//    @GetMapping("/page/{pageId}")
//    public List<UIPageAction> getByPageId(@PathVariable Long pageId) {
//        return service.getByPageId(pageId);
//    }
//
//    @DeleteMapping("/{id}")
//    public void delete(@PathVariable Long id) {
//        service.delete(id);
//    }
}