package com.hospitalmanagementsystem.hospital.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // No sessions - JWT only
                .authorizeHttpRequests(auth -> auth

                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()

                        // Admin only
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Doctor management
                        .requestMatchers(HttpMethod.POST,   "/api/doctors").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/doctors/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/doctors/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/doctors/**").authenticated()

                        // Appointments
                        .requestMatchers(HttpMethod.POST, "/api/appointments").hasAnyRole("PATIENT","ADMIN")
                        .requestMatchers("/api/appointments/**").authenticated()

                        // Medical records
                        .requestMatchers(HttpMethod.POST, "/api/medical-records").hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.GET,  "/api/medical-records/**").hasAnyRole("DOCTOR","ADMIN")

                        // Patients
                        .requestMatchers(HttpMethod.GET, "/api/patients/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/patients").hasRole("ADMIN")

                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )

                // Run our JWT filter before Spring's default auth filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();   // industry standard for password hashing
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
