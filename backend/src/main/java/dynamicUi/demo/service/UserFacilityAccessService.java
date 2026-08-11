package dynamicUi.demo.service;

import dynamicUi.demo.entity.AccessStatus;
import dynamicUi.demo.entity.UserFacilityAccess;
import dynamicUi.demo.repoistory.UserFacilityAccessRepository;
import dynamicUi.demo.security.AppUser;
import dynamicUi.demo.security.AppUserRepository;
import dynamicUi.demo.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFacilityAccessService {

    private final UserFacilityAccessRepository userFacilityAccessRepository;
    private final AppUserRepository appUserRepository;
    private final JwtUtil jwtUtil;

    //create userfacility request
    public UserFacilityAccess createAccessRequest(
            String username,
            String facilityId
    ) {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with username: " + username
                        )
                );

        UserFacilityAccess access = UserFacilityAccess.builder()
                .user(user)
                .facilityId(facilityId)
                .status(AccessStatus.PENDING)
                .build();

        return userFacilityAccessRepository.save(access);
    }
}