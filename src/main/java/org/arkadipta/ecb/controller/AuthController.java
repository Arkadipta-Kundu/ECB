package org.arkadipta.ecb.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.arkadipta.ecb.dto.auth.JwtResponse;
import org.arkadipta.ecb.dto.auth.LoginRequest;
import org.arkadipta.ecb.dto.auth.RefreshTokenRequest;
import org.arkadipta.ecb.dto.auth.SignupRequest;
import org.arkadipta.ecb.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        JwtResponse response = authService.register(signupRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        JwtResponse response = authService.refreshToken(refreshTokenRequest);
        return ResponseEntity.ok(response);
    }
}