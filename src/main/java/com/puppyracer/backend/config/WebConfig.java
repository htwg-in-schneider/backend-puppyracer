@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins:http://localhost:5173,https://htwg-in-schneider.github.io}")
    private String[] allowedOrigins;
    
    @Value("${app.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String[] allowedMethods;
    
    @Value("${app.cors.allowed-headers:*}")
    private String[] allowedHeaders;
    
    @Value("${app.cors.max-age:3600}")
    private long maxAge;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods(allowedMethods)
                .allowedHeaders(allowedHeaders)
                .exposedHeaders("Authorization")  // Wichtig für JWT
                .allowCredentials(true)
                .maxAge(maxAge);
    }
}