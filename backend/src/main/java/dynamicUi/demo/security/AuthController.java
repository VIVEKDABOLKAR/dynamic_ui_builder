package dynamicUi.demo.security;

import dynamicUi.demo.entity.AccessStatus;
import dynamicUi.demo.entity.Facility;
import dynamicUi.demo.entity.UserFacilityAccess;
import dynamicUi.demo.repoistory.UserFacilityAccessRepository;
import dynamicUi.demo.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AppUserRepository userRepo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserFacilityAccessRepository accessRepo;
    private final FacilityService facilityService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userRepo.findByUsername(req.username()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already taken");
        }

        AppUser user = AppUser.builder()
                .username(req.username())
                .password(encoder.encode(req.password()))
                .role(req.role() != null ? req.role() : Role.ROLE_VIEWER.name())
                .build();

        userRepo.save(user);

        // Create a PENDING access request for each selected facility
        if (req.facilityIds() != null) {
            for (String facilityId : req.facilityIds()) {
                UserFacilityAccess access = UserFacilityAccess.builder()
                        .user(user)
                        .facilityId(facilityId)
                        .status(AccessStatus.PENDING)
                        .build();
                accessRepo.save(access);
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User registered. Facility access is pending admin approval.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        AppUser user = userRepo.findByUsername(req.username()).orElseThrow(() -> new RuntimeException("User not found"));

        List<Facility> allowedFacility = facilityService.findAccessibleFacilities(user.getUsername());
        Set<String> allowedFacilityIds = allowedFacility.stream()
                .map(Facility::getId)
                .collect(Collectors.toSet());


        String selectedFacilityId;
        if (req.facilityId() != null) {
            if (!allowedFacilityIds.contains(req.facilityId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("User does not have approved access to facility: " + req.facilityId());
            }
            selectedFacilityId = req.facilityId();
        } else {
            selectedFacilityId = allowedFacility.isEmpty() ? null : allowedFacility.getFirst().getId();
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole(), selectedFacilityId);
        return ResponseEntity.ok(new TokenResponse(token, user.getRole(), selectedFacilityId,  allowedFacilityIds.stream().toList()));
    }

    public record LoginRequest(String username, String password, String facilityId) {}
    public record RegisterRequest(String username, String password, String  role, List<String> facilityIds) {}
    public record TokenResponse(String token, String role, String facilityId, List<String> availableFacilityIds) {}
}