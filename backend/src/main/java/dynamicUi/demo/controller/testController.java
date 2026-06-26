package dynamicUi.demo.controller;

import dynamicUi.demo.dto.PageJsonDTO;
import dynamicUi.demo.dto.UIComponentDTO;
import dynamicUi.demo.service.PageAssemblerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin("*")
@RequiredArgsConstructor
public class testController {

    private final PageAssemblerService pageAssemblerService;

    /**
     * this is test api for testing getting correct response coming form frontend
     * by printing it on console and sending back to frontend
     * @param requestBody
     * @return
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createComponent(
            @RequestBody Map<String, Object> requestBody
    ) {

        // print request in console
        System.out.println("Received Request Body:");
        System.out.println(requestBody);

        // return same request back to frontend
        return ResponseEntity.ok(requestBody);
    }

    @GetMapping("/{pageCode}")
    public PageJsonDTO getAssemblePageJson(@PathVariable String pageCode) {
        PageJsonDTO pageJsonDTO = pageAssemblerService.getPageAssembledByPageCode(pageCode);

        return pageJsonDTO;
    }

}
