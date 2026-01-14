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
            
            .authorizeHttpRequests(auth -> auth
                // OPTIONS für CORS (SEHR WICHTIG!)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // ÖFFENTLICHE ENDPUNKTE
                .requestMatchers("/", "/error", "/actuator/health").permitAll()
                .requestMatchers("/h2-console/**").permitAll()  // Für H2
                
                // PROFILE ENDPOINT (für Frontend Admin-Check)
                .requestMatchers(HttpMethod.GET, "/api/profile").authenticated()  // ← NEU!
                .requestMatchers(HttpMethod.PUT, "/api/profile").authenticated()
                
                // PRODUKTE LESEN (alle)
                .requestMatchers(HttpMethod.GET, "/api/product/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/category/**").permitAll()
                
                // PRODUKTE SCHREIBEN (nur authentifiziert)
                .requestMatchers(HttpMethod.POST, "/api/product/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/product/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/product/**").authenticated()
                
                // BESTELLUNGEN (authentifiziert)
                .requestMatchers("/api/order/**").authenticated()
                .requestMatchers("/api/cart/**").authenticated()
                
                // ALLE ANDEREN erfordern Authentifizierung
                .anyRequest().authenticated()
            )
            
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(Customizer.withDefaults())
            )
            
            .headers(headers -> headers
                .frameOptions(frame -> frame.disable())
            )
            
            .build();
    }
}