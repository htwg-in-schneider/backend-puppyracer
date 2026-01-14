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
            .cors(Customizer.withDefaults())  // Wichtig für Spring Security CORS Integration
            
            .csrf(csrf -> csrf.disable())
            
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // ============ CORS KONFIGURATION ============
            // ZUERST CORS OPTIONS erlauben
            .authorizeHttpRequests(auth -> auth
                // SEHR WICHTIG: OPTIONS vor allen anderen Regeln
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // ÖFFENTLICHE ENDPUNKTE
                .requestMatchers("/").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                
                // PRODUKTE LESEN (alle)
                .requestMatchers(HttpMethod.GET, "/api/product/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/category/**").permitAll()
                
                // PRODUKTE SCHREIBEN (nur authentifiziert)
                .requestMatchers(HttpMethod.POST, "/api/product/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/product/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/product/**").authenticated()
                
                // ALLE ANDEREN REQUESTES erfordern Authentifizierung
                .anyRequest().authenticated()
            )
            
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(Customizer.withDefaults())
            )
            
            .headers(headers -> headers
                .frameOptions(frame -> frame.disable())  // Für H2 Console wenn nötig
            )
            
            .build();
    }
}