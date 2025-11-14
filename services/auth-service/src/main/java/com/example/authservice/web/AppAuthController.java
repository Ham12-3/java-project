package com.example.authservice.web;

import com.example.authservice.domain.AuthUser;
import com.example.authservice.service.CustomerAppService;
import com.example.authservice.service.EmailMfaService;
import com.example.authservice.service.JwtService;
import com.example.authservice.service.UserService;
import com.example.authservice.web.dto.CustomerLoginRequest;
import com.example.authservice.web.dto.CustomerLoginResponse;
import com.example.authservice.web.dto.CustomerLoginVerifyRequest;
import com.example.authservice.web.dto.MfaChallengeResponse;
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
@RequestMapping("/api/app/auth")
public class AppAuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final EmailMfaService emailMfaService;
    private final JwtService jwtService;
    private final CustomerAppService customerAppService;

    public AppAuthController(UserService userService,
                             PasswordEncoder passwordEncoder,
                             EmailMfaService emailMfaService,
                             JwtService jwtService,
                             CustomerAppService customerAppService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.emailMfaService = emailMfaService;
        this.jwtService = jwtService;
        this.customerAppService = customerAppService;
    }

    @PostMapping("/login")
    public MfaChallengeResponse login(@Valid @RequestBody CustomerLoginRequest request) {
        AuthUser user = authenticate(request.getUsername(), request.getPassword());
        var issued = emailMfaService.issue(user);
        return new MfaChallengeResponse(issued.challengeId(), issued.expiresAt());
    }

    @PostMapping("/login/verify")
    public CustomerLoginResponse verify(@Valid @RequestBody CustomerLoginVerifyRequest request) {
        if (!emailMfaService.verify(request.getUsername(), request.getCode())) {
            throw new IllegalArgumentException("Invalid or expired code");
        }
        String token = jwtService.generateToken(request.getUsername());
        Long customerId = customerAppService.findCustomerByUsername(request.getUsername()).getId();
        return new CustomerLoginResponse(token, customerId);
    }

    private AuthUser authenticate(String username, String password) {
        AuthUser user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return user;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
