package dynamicUi.demo.controller;

import dynamicUi.demo.dto.UserRoleResponse;
import dynamicUi.demo.dto.UserRoleUpdateRequest;
import dynamicUi.demo.service.UserRoleAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserRoleAdminController {

    private final UserRoleAdminService service;

    @GetMapping
    public List<UserRoleResponse> getAllUsers() {
        return service.getAllUsers();
    }

    @PutMapping("/{userId}/role")
    public UserRoleResponse updateRole(@PathVariable Long userId, @RequestBody UserRoleUpdateRequest request) {
        return service.updateRole(userId, request.role());
    }
}
