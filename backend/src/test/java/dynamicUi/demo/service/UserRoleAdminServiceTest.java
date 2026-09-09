package dynamicUi.demo.service;

import dynamicUi.demo.dto.UserRoleResponse;
import dynamicUi.demo.exception.UserNotFoundException;
import dynamicUi.demo.security.AppUser;
import dynamicUi.demo.security.AppUserRepository;
import dynamicUi.demo.security.Role;
import org.apache.catalina.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRoleAdminServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private UserRoleAdminService service;

    @Test
    @DisplayName("getAllUsers returns all users as UserRoleResponse")
    void getAllUsersReturnsAllUsers() {
        AppUser admin = AppUser.builder()
                .id(1L)
                .username("admin")
                .role(Role.ROLE_ADMIN.name())
                .build();

        AppUser user = AppUser.builder()
                .id(2L)
                .username("john")
                .role("ROLE_USER")
                .build();

        when(appUserRepository.findAll()).thenReturn(List.of(admin, user));

        List<UserRoleResponse> result = service.getAllUsers();

        assertThat(result).hasSize(2);

        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).username()).isEqualTo("admin");
        assertThat(result.get(0).role()).isEqualTo(Role.ROLE_ADMIN.name());

        assertThat(result.get(1).id()).isEqualTo(2L);
        assertThat(result.get(1).username()).isEqualTo("john");
        assertThat(result.get(1).role()).isEqualTo("ROLE_USER");

        verify(appUserRepository).findAll();
    }

    @Test
    @DisplayName("getAllUsers returns empty list when no users exist")
    void getAllUsersReturnsEmptyList() {
        when(appUserRepository.findAll()).thenReturn(List.of());

        List<UserRoleResponse> result = service.getAllUsers();

        assertThat(result).isEmpty();
        verify(appUserRepository).findAll();
    }

    @Test
    @DisplayName("updateRole updates and returns the user's new role")
    void updateRoleUpdatesUserRole() {
        AppUser user = AppUser.builder()
                .id(1L)
                .username("john")
                .role("ROLE_USER")
                .build();

        when(appUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(appUserRepository.save(user)).thenReturn(user);

        UserRoleResponse result =
                service.updateRole(1L, Role.ROLE_ADMIN.name());

        assertThat(user.getRole()).isEqualTo(Role.ROLE_ADMIN.name());
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.username()).isEqualTo("john");
        assertThat(result.role()).isEqualTo(Role.ROLE_ADMIN.name());

        verify(appUserRepository).findById(1L);
        verify(appUserRepository).save(user);
    }

    @Test
    @DisplayName("updateRole throws 404 when user does not exist")
    void updateRoleThrowsWhenUserNotFound() {
        when(appUserRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.updateRole(99L, Role.ROLE_ADMIN.name()))
                .isInstanceOf(UserNotFoundException.class);

        verify(appUserRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateRole prevents demoting the last remaining administrator")
    void updateRolePreventsDemotingLastAdmin() {
        AppUser admin = AppUser.builder()
                .id(1L)
                .username("admin")
                .role(Role.ROLE_ADMIN.name())
                .build();

        when(appUserRepository.findById(1L))
                .thenReturn(Optional.of(admin));

        when(appUserRepository.findAll())
                .thenReturn(List.of(admin));

        assertThatThrownBy(() ->
                service.updateRole(1L, "ROLE_USER"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException ex =
                            (ResponseStatusException) exception;

                    assertThat(ex.getStatusCode())
                            .isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ex.getReason())
                            .contains("Cannot remove the last remaining administrator.");
                });

        verify(appUserRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateRole allows demoting an administrator when another administrator exists")
    void updateRoleAllowsDemotionWhenAnotherAdminExists() {
        AppUser admin1 = AppUser.builder()
                .id(1L)
                .username("admin1")
                .role(Role.ROLE_ADMIN.name())
                .build();

        AppUser admin2 = AppUser.builder()
                .id(2L)
                .username("admin2")
                .role(Role.ROLE_ADMIN.name())
                .build();

        when(appUserRepository.findById(1L))
                .thenReturn(Optional.of(admin1));

        when(appUserRepository.findAll())
                .thenReturn(List.of(admin1, admin2));

        when(appUserRepository.save(admin1))
                .thenReturn(admin1);

        UserRoleResponse result =
                service.updateRole(1L, "ROLE_USER");

        assertThat(admin1.getRole()).isEqualTo("ROLE_USER");
        assertThat(result.role()).isEqualTo("ROLE_USER");

        verify(appUserRepository).save(admin1);
    }

    @Test
    @DisplayName("updateRole allows changing a non-admin user's role")
    void updateRoleAllowsNonAdminRoleChange() {
        AppUser user = AppUser.builder()
                .id(1L)
                .username("john")
                .role("ROLE_USER")
                .build();

        when(appUserRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(appUserRepository.save(user))
                .thenReturn(user);

        UserRoleResponse result =
                service.updateRole(1L, "ROLE_MANAGER");

        assertThat(result.role()).isEqualTo("ROLE_MANAGER");
        assertThat(user.getRole()).isEqualTo("ROLE_MANAGER");

        verify(appUserRepository, never()).findAll();
        verify(appUserRepository).save(user);
    }
}