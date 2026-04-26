package com.example.urlshortener;

import com.example.urlshortener.auth.*;
import com.example.urlshortener.user.Role;
import com.example.urlshortener.user.User;
import com.example.urlshortener.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterUser() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("test");
        request.setPassword("StrongPass123");

        when(userRepository.findByUsername("test"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encoded-password");

        when(jwtService.generateAccessToken(anyString(), any()))
                .thenReturn("fake-token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
    }

    @Test
    void shouldLoginUser() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("encoded-password");
        user.setRole(Role.ROLE_USER);

        when(userRepository.findByUsername("test"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(true);

        when(jwtService.generateAccessToken(anyString(), any()))
                .thenReturn("fake-token");

        LoginRequest request = new LoginRequest();
        request.setUsername("test");
        request.setPassword("StrongPass123");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
    }
}
