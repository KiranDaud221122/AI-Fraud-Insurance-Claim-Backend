package com.fraudclaimdetector.claim_user_service.controller;


import com.fraudclaimdetector.claim_user_service.dto.AuthResponse;
import com.fraudclaimdetector.claim_user_service.dto.LoginRequest;
import com.fraudclaimdetector.claim_user_service.dto.RegisterRequest;
import com.fraudclaimdetector.claim_user_service.dto.UserResponse;
import com.fraudclaimdetector.claim_user_service.service.AuthService;
import com.fraudclaimdetector.claim_user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse user = userService.createUser(request);
        return ResponseEntity.status(201).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}