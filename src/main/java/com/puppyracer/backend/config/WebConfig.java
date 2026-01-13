package com.puppyracer.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins:http://localhost:5173}")
    private String[] allowedOrigins;
    
    @Value("${app.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String[] allowedMethods;
    
    @Value("${app.cors.max-age:3600}")
    private long maxAge;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods(allowedMethods)
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(maxAge);
    }
    
    // WICHTIG für H2 Console in Produktion deaktivieren
    @Bean
    public WebMvcConfigurer h2ConsoleDisabler() {
        return new WebMvcConfigurer() {
            @Override
            public void addViewControllers(org.springframework.web.servlet.config.annotation.ViewControllerRegistry registry) {
                // Optional: H2 Console in Produktion deaktivieren
                String profile = System.getenv("SPRING_PROFILES_ACTIVE");
                if ("prod".equals(profile) || "production".equals(profile)) {
                    // H2 Console auf 404 setzen
                    registry.addViewController("/h2-console").setViewName("forward:/error/404");
                }
            }
        };
    }
}