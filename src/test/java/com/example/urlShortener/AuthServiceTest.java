package com.example.urlShortener;

import com.example.urlShortener.auth.AuthRequest;
import com.example.urlShortener.auth.AuthResponse;
import com.example.urlShortener.auth.AuthService;
import com.example.urlShortener.auth.JwtService;
import com.example.urlShortener.user.User;
import com.example.urlShortener.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterUser() {

        AuthRequest request = new AuthRequest();
        request.setUsername("test");
        request.setPassword("Password1"); // ✅ валідний пароль

        when(userRepository.findByUsername("test"))
                .thenReturn(Optional.empty());

        when(encoder.encode(any()))
                .thenReturn("encoded");

        when(jwtService.generateAccessToken(any(), any()))
                .thenReturn("access");

        when(jwtService.generateRefreshToken(any()))
                .thenReturn("refresh");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
    }

    @Test
    void shouldThrowIfUserExists() {

        AuthRequest request = new AuthRequest();
        request.setUsername("test");
        request.setPassword("Password1");

        when(userRepository.findByUsername("test"))
                .thenReturn(Optional.of(new User()));

        assertThrows(RuntimeException.class, () ->
                authService.register(request));
    }
}
