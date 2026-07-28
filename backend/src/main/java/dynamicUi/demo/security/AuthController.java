package dynamicUi.demo.security;

import dynamicUi.demo.entity.AccessStatus;
import dynamicUi.demo.entity.UserFacilityAccess;
import dynamicUi.demo.repoistory.UserFacilityAccessRepository;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AppUserRepository userRepo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserFacilityAccessRepository accessRepo;

    public AuthController(AppUserRepository userRepo,
                          PasswordEncoder encoder,
                          AuthenticationManager authManager,
                          JwtUtil jwtUtil,
                          UserFacilityAccessRepository accessRepo) {
        this.userRepo    = userRepo;
        this.encoder     = encoder;
        this.authManager = authManager;
        this.jwtUtil     = jwtUtil;
        this.accessRepo  = accessRepo;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userRepo.findByUsername(req.username()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already taken");
        }

        AppUser user = AppUser.builder()
                .username(req.username())
                .password(encoder.encode(req.password()))
                .role(req.role() != null ? req.role() : Role.ROLE_VIEWER)
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
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return ResponseEntity.ok(new TokenResponse(token, user.getRole().name()));
    }

    public record LoginRequest(String username, String password) {}
    public record RegisterRequest(String username, String password, Role role, List<String> facilityIds) {}
    public record TokenResponse(String token, String role) {}
}