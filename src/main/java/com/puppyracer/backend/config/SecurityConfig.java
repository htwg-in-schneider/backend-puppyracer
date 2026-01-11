package com.puppyracer.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // ============ AUTORISIERUNG ============
            .authorizeHttpRequests(auth -> auth
                // 🔓 ÖFFENTLICHE ENDPUNKTE
                .requestMatchers("/").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/error").permitAll()
                
                // 🔓 PRODUKTE LESEN (alle) - laut PDF Seite 36
                .requestMatchers(HttpMethod.GET, "/api/product/**").permitAll()
                
                // 🔐 PRODUKTE SCHREIBEN (nur authentifiziert) - laut PDF Seite 36
                .requestMatchers(HttpMethod.POST, "/api/product/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/product/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/product/**").authenticated()
                
                // 🔐 USER MANAGEMENT (authentifiziert)
                // Die ROLLE prüft der Controller selbst! (PDF Seite 34-35)
                .requestMatchers("/api/users/**").authenticated()
                
                // 🔐 ORDERS ADMIN (authentifiziert)
                // Die ROLLE prüft der Controller selbst!
                .requestMatchers("/api/orders/admin/**").authenticated()
                
                // 🔐 ORDERS USER (authentifizierte User)
                .requestMatchers("/api/orders/my-orders").authenticated()
                
                // 🔐 PROFIL (authentifizierte User)
                .requestMatchers("/api/profile").authenticated()
                
                // 🔐 CHECKOUT (authentifizierte User)
                .requestMatchers(HttpMethod.POST, "/api/product/checkout").authenticated()
                
                // 🔐 REVIEWS (authentifizierte User)
                .requestMatchers(HttpMethod.POST, "/api/review/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/review/**").authenticated()
                
                // 🔐 ALLE ANDEREN API ENDPUNKTE
                .requestMatchers("/api/**").authenticated()
                
                // 🔓 ALLE ANDEREN REQUESTS
                .anyRequest().permitAll()
            )
            
            // OAuth2/JWT Resource Server - laut PDF Seite 29
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(Customizer.withDefaults())
            )
            
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            )
            
            .build();
    }
}