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
                
                // 🔓 PRODUKTE LESEN (alle)
                .requestMatchers(HttpMethod.GET, "/api/product/**").permitAll()
                
                // NEU (mit Rollenprüfung):
                .requestMatchers(HttpMethod.POST, "/api/product/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/product/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/product/**").hasAuthority("ADMIN")
                .requestMatchers("/api/users/**").hasAuthority("ADMIN")
                .requestMatchers("/api/orders/admin/**").hasAuthority("ADMIN")
                
                // 🔐 PROFIL (nur authentifiziert)
                .requestMatchers(HttpMethod.GET, "/api/profile").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/profile").authenticated()
                
                // 🔐 USER MANAGEMENT (authentifiziert) ← Rolle prüft Controller
                .requestMatchers("/api/users/**").authenticated()
                
                
                // 🔐 ORDERS USER (authentifiziert)
                .requestMatchers(HttpMethod.GET, "/api/orders/my-orders").authenticated()
                
                // 🔐 ALLE ANDEREN API ENDPUNKTE
                .requestMatchers("/api/**").authenticated()
                
                // 🔓 ALLE ANDEREN REQUESTS
                .anyRequest().permitAll()
            )
            
            // KEIN JWT Converter! Einfache JWT Validierung
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(Customizer.withDefaults())
            )
            
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            )
            
            .build();
    }
}