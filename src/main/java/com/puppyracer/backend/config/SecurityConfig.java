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
            .cors(Customizer.withDefaults())  // WICHTIG: Laut Go-Live-Checkliste
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // ============ AUTHORIZATION ============
            .authorizeHttpRequests(auth -> auth
                // ÖFFENTLICHE ENDPUNKTE
                .requestMatchers("/").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/actuator/health").permitAll()  // Für Deployment-Health-Checks
                
                // PRODUKTE LESEN (alle)
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/product/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/category/**").permitAll()
                
                // PRODUKTE SCHREIBEN (nur authentifiziert)
                .requestMatchers(HttpMethod.POST, "/api/product/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/product/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/product/**").authenticated()
                
                // USER MANAGEMENT (authentifiziert)
                .requestMatchers("/api/users/**").authenticated()
                
                // ORDERS ADMIN (authentifiziert)
                .requestMatchers("/api/orders/admin/**").authenticated()
                
                // ORDERS USER (authentifizierte User)
                .requestMatchers("/api/orders/my-orders").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/orders").authenticated()
                
                // PROFIL (authentifizierte User)
                .requestMatchers("/api/profile").authenticated()
                
                // CHECKOUT (authentifizierte User)
                .requestMatchers(HttpMethod.POST, "/api/checkout").authenticated()
                
                // REVIEWS (authentifizierte User)
                .requestMatchers(HttpMethod.POST, "/api/review/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/review/**").authenticated()
                
                // STATISCHE RESSOURCEN
                .requestMatchers(HttpMethod.GET, "/images/**").permitAll()
                
                // ALLE ANDEREN API ENDPUNKTE
                .requestMatchers("/api/**").authenticated()
                
                // ALLE ANDEREN REQUESTS
                .anyRequest().permitAll()
            )
            
            // OAuth2/JWT Resource Server
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(Customizer.withDefaults())
            )
            
            // H2 Console Frame Options
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            )
            
            .build();
    }
}