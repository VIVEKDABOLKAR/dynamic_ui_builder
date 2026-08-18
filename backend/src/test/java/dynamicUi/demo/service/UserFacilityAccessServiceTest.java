package dynamicUi.demo.service;

import dynamicUi.demo.entity.AccessStatus;
import dynamicUi.demo.entity.UserFacilityAccess;
import dynamicUi.demo.repoistory.UserFacilityAccessRepository;
import dynamicUi.demo.security.AppUser;
import dynamicUi.demo.security.AppUserRepository;
import dynamicUi.demo.security.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link UserFacilityAccessService}.
 */
@ExtendWith(MockitoExtension.class)
class UserFacilityAccessServiceTest {

    @Mock
    private UserFacilityAccessRepository userFacilityAccessRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserFacilityAccessService service;

    @Test
    @DisplayName("createAccessRequest creates a PENDING request for an existing user")
    void createAccessRequestCreatesPendingRequest() {
        AppUser user = AppUser.builder().id(1L).username("viewer-user").password("x").role("ROLE_VIEWER").build();
        when(appUserRepository.findByUsername("viewer-user")).thenReturn(Optional.of(user));
        when(userFacilityAccessRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserFacilityAccess result = service.createAccessRequest("viewer-user", "F1");

        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getFacilityId()).isEqualTo("F1");
        assertThat(result.getStatus()).isEqualTo(AccessStatus.PENDING);

        ArgumentCaptor<UserFacilityAccess> captor = ArgumentCaptor.forClass(UserFacilityAccess.class);
        verify(userFacilityAccessRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(AccessStatus.PENDING);
    }

    @Test
    @DisplayName("createAccessRequest throws when the user does not exist")
    void createAccessRequestThrowsWhenUserMissing() {
        when(appUserRepository.findByUsername("unknown-user")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createAccessRequest("unknown-user", "F1"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("unknown-user");

        verify(userFacilityAccessRepository, never()).save(any());
    }
}
