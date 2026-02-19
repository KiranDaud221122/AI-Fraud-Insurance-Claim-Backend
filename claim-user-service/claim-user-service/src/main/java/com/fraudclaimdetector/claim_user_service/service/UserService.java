package com.fraudclaimdetector.claim_user_service.service;
import com.fraudclaimdetector.claim_user_service.dto.RegisterRequest;
import com.fraudclaimdetector.claim_user_service.dto.UserResponse;
import com.fraudclaimdetector.claim_user_service.entity.User;
import com.fraudclaimdetector.claim_user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(RegisterRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName().trim())
                .phoneNumber(request.getPhoneNumber())
                .build();

        return mapToResponse(userRepository.saveAndFlush(user));
    }

    public UserResponse getUserById(String id) {
        return userRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public UserResponse updateUser(String userId, RegisterRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String email = request.getEmail().trim().toLowerCase();

        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already in use");
        }

        user.setEmail(email);
        user.setFullName(request.getFullName().trim());
        user.setPhoneNumber(request.getPhoneNumber() != null ? request.getPhoneNumber().trim() : null);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return mapToResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(userId);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt() : LocalDateTime.now())
                .updatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt() : LocalDateTime.now())
                .build();
    }

}