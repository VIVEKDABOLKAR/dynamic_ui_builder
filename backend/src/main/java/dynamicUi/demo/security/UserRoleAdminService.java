package dynamicUi.demo.service;

import dynamicUi.demo.dto.UserRoleResponse;
import dynamicUi.demo.security.AppUser;
import dynamicUi.demo.security.AppUserRepository;
import dynamicUi.demo.security.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRoleAdminService {

    private final AppUserRepository appUserRepository;

    @Transactional(readOnly = true)
    public List<UserRoleResponse> getAllUsers() {
        return appUserRepository.findAll()
                .stream()
                .map(user -> new UserRoleResponse(user.getId(), user.getUsername(), user.getRole()))
                .toList();
    }

    @Transactional
    public UserRoleResponse updateRole(Long userId, Role newRole) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId));

        // Guard: don't allow demoting the last remaining admin
        if (user.getRole() == Role.ROLE_ADMIN && newRole != Role.ROLE_ADMIN) {
            long adminCount = appUserRepository.findAll().stream()
                    .filter(u -> u.getRole() == Role.ROLE_ADMIN)
                    .count();
            if (adminCount <= 1) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Cannot remove the last remaining administrator.");
            }
        }

        user.setRole(newRole);
        AppUser saved = appUserRepository.save(user);

        return new UserRoleResponse(saved.getId(), saved.getUsername(), saved.getRole());
    }
}
