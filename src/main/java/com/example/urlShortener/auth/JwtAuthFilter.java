package com.example.urlshortener.auth;

import com.example.urlShortener.user.User;
import com.example.urlShortener.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtService jwtService,
                         UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // 🔴 1. Якщо немає токена — пропускаємо
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            final String username = jwtService.extractUsername(token);

            // 🔴 2. Якщо користувач знайдений і ще не аутентифікований
            if (username != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                // 🔴 3. Перевірка валідності токена
                if (jwtService.isValid(token)) {

                    // 🔴 4. Дістаємо користувача з БД
                    User user = userRepository.findByUsername(username)
                            .orElseThrow();

                    // 🔴 5. Дістаємо роль з токена
                    String role = jwtService.extractRole(token);

                    // 🔴 6. Створюємо authentication з ролями
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    user, // 🔥 тепер передаємо User
                                    null,
                                    List.of(new SimpleGrantedAuthority(role))
                            );

                    // 🔴 7. Додаємо деталі
                    auth.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    // 🔴 8. Кладемо в SecurityContext
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(auth);
                }
            }

        } catch (Exception e) {
            // 🔴 якщо токен битий — просто пропускаємо (не падаємо)
        }

        filterChain.doFilter(request, response);
    }
}
