package com.hospitalmanagementsystem.hospital.service;

import com.hospitalmanagementsystem.hospital.dto.request.LoginRequest;
import com.hospitalmanagementsystem.hospital.dto.request.RegisterRequest;
import com.hospitalmanagementsystem.hospital.dto.response.LoginResponse;
import com.hospitalmanagementsystem.hospital.exception.DuplicateEmailException;
import com.hospitalmanagementsystem.hospital.model.User;
import com.hospitalmanagementsystem.hospital.model.User.Role;
import com.hospitalmanagementsystem.hospital.repository.UserRepository;
import com.hospitalmanagementsystem.hospital.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf(request.getRole()))
                .build();

        User saved = userRepository.save(user);
        log.info("New user registered: {} ({})", saved.getEmail(), saved.getRole());

        String token = jwtUtil.generateToken(saved.getId(), saved.getEmail(), saved.getRole().name());
        return new LoginResponse(token, saved.getEmail(), saved.getRole().name());
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        // BCrypt comparison — never compare plain text passwords
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        log.info("User logged in: {}", user.getEmail());
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        return new LoginResponse(token, user.getEmail(), user.getRole().name());
    }
}
