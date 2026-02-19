package com.fraudclaimdetector.claim_user_service.service;


import com.fraudclaimdetector.claim_user_service.dto.AuthResponse;
import com.fraudclaimdetector.claim_user_service.dto.LoginRequest;
import com.fraudclaimdetector.claim_user_service.dto.RegisterRequest;
import com.fraudclaimdetector.claim_user_service.entity.User;
import com.fraudclaimdetector.claim_user_service.repository.UserRepository;
import com.fraudclaimdetector.claim_user_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        // Prevent duplicate email
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        // Build and save new user
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName().trim())
                .phoneNumber(request.getPhoneNumber())
                .build();

        User savedUser = userRepository.save(user);

        // Generate token using your existing JwtService
        String token = JwtService.generateToken(savedUser);

        // Return token + user info
        return AuthResponse.builder()
                .token(token)
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        // Find user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Generate token using your existing JwtService
        String token = JwtService.generateToken(user);

        // Return token + user info
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}