package com.hospitalmanagementsystem.hospital.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        // No token — let the request pass
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtUtil.validateAndExtract(token);
            Long userId = jwtUtil.extractUserId(claims);
            String role = jwtUtil.extractRole(claims);
            String email = jwtUtil.extractEmail(claims);

            // Tell Spring Security: this user is authenticated with this role
            // "ROLE_" prefix is required by Spring Security convention
            var auth = new UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE" + role))
            );

            // Store the auth info in SecurityContext — now controllers can access it
            auth.setDetails(userId); // Store userId in details for easy access in controllers

            SecurityContextHolder.getContext().setAuthentication(auth);
            log.debug("Authenticated user: {} role: {}", email, role);
        } catch (Exception e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            // Invalid token — clear context and proceed (will be blocked by security config)
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(request, response);
    }
}
