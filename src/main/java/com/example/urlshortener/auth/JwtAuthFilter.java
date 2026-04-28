package com.example.urlshortener.auth;

import com.example.urlshortener.user.User;
import com.example.urlshortener.user.UserRepository;
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

        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            final String username = jwtService.extractUsername(token);

            
            if (username != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                
                if (jwtService.isValid(token)) {

                    
                    User user = userRepository.findByUsername(username)
                            .orElseThrow();

                    
                    String role = jwtService.extractRole(token);

                    
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    user, // 🔥 тепер передаємо User
                                    null,
                                    List.of(new SimpleGrantedAuthority(role))
                            );

                    
                    auth.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    ]
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(auth);
                }
            }

        } catch (Exception e) {
            
        }

        filterChain.doFilter(request, response);
    }
}
