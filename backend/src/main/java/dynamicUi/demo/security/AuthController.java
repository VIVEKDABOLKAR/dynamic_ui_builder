package dynamicUi.demo.security;

import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AppUserRepository userRepo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public AuthController(AppUserRepository userRepo,
                          PasswordEncoder encoder,
                          AuthenticationManager authManager,
                          JwtUtil jwtUtil) {
        this.userRepo    = userRepo;
        this.encoder     = encoder;
        this.authManager = authManager;
        this.jwtUtil     = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userRepo.findByUsername(req.username()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already taken");
        }

        AppUser user = AppUser.builder()
                .username(req.username())
                .password(encoder.encode(req.password()))  // BCrypt hash stored here
                .role(req.role() != null ? req.role() : Role.ROLE_VIEWER)
                .build();

        userRepo.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered");
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
    public record RegisterRequest(String username, String password, Role role) {}
    public record TokenResponse(String token, String role) {}
}