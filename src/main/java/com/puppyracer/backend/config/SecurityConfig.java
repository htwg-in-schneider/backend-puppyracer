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
            .cors(Customizer.withDefaults())  // WICHTIG: Laut Checkliste
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // ============ AUTHORIZATION ============
            .authorizeHttpRequests(auth -> auth
                // OPTIONS METHOD FÜR CORS PREFLIGHT ERLAUBEN (SEHR WICHTIG!)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // ÖFFENTLICHE ENDPUNKTE
                .requestMatchers("/").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                
                // H2 CONSOLE STATISCHE RESSOURCEN
                .requestMatchers("/h2-console/**").permitAll()
                
                // PRODUKTE LESEN (alle)
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/product/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/category/**").permitAll()
                
                // ... Rest deiner Authorization bleibt gleich ...
                
                // PRODUKTE SCHREIBEN (nur authentifiziert)
                .requestMatchers(HttpMethod.POST, "/api/product/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/product/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/product/**").authenticated()
                
                // ... Rest deiner Regeln ...
            )
            
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(Customizer.withDefaults())
            )
            
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            )
            
            .build();
    }
}