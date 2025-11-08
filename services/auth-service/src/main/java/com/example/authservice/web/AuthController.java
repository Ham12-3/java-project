package com.example.authservice.web;

import com.example.authservice.domain.AuthUser;
import com.example.authservice.service.JwtService;
import com.example.authservice.service.MfaService;
import com.example.authservice.service.UserService;
import com.example.authservice.web.dto.LoginRequest;
import com.example.authservice.web.dto.LoginResponse;
import com.example.authservice.web.dto.RegisterRequest;
import com.example.authservice.web.dto.RegisterResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MfaService mfaService;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtService jwtService, MfaService mfaService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.mfaService = mfaService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserService.RegistrationResult result = userService.register(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.isMfaEnabled()
        );
        RegisterResponse response = new RegisterResponse(
                result.user().getUsername(),
                result.user().isMfaEnabled(),
                result.mfaSecret()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthUser user = userService.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        if (user.isMfaEnabled() && !mfaService.verifyCode(user.getMfaSecret(), request.getMfaCode())) {
            throw new IllegalArgumentException("Invalid MFA code");
        }

        String token = jwtService.generateToken(user.getUsername());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
