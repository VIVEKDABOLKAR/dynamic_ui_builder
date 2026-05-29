package dynamicUi.demo.controller;

import dynamicUi.demo.dto.UIComponentDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin("*")
public class testController {

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
}
