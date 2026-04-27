package com.example.urlshortener.auth;

import com.example.urlshortener.exception.BadRequestException;
import com.example.urlshortener.user.Role;
import com.example.urlshortener.user.User;
import com.example.urlshortener.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // =========================
    // REGISTER
    // =========================
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BadRequestException("User exists");
        }

        if (!isValidPassword(request.getPassword())) {
            throw new BadRequestException("Weak password");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_USER);

        user.setCreatedAt(LocalDateTime.now()); // 🔥 FIX

        userRepository.save(user);

        return new AuthResponse(
                jwtService.generateAccessToken(user.getUsername(), user.getRole().name()),
                jwtService.generateRefreshToken(user.getUsername())
        );
    }

    // =========================
    // LOGIN
    // =========================
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Wrong password");
        }

        return new AuthResponse(
                jwtService.generateAccessToken(user.getUsername(), user.getRole().name()),
                jwtService.generateRefreshToken(user.getUsername())
        );
    }

    // =========================
    // PASSWORD VALIDATION
    // =========================
    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$");
    }
}