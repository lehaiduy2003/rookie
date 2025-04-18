package com.example.assignment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

import static com.example.assignment.constant.SecurityURL.PUBLIC_URLS;

/**
 * Web configuration class to set up content negotiation.
 * This class configures the default content type for the application.
 * It implements the WebMvcConfigurer interface to customize the default settings.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String ENVIRONMENT = System.getProperty("spring.profiles.active");
    private static final String[] ALLOWED_ORIGINS = {"http://localhost:3000", "http://localhost:5173"};

    /**
     * Configures content negotiation for the application.
     * This method sets the default content type to JSON.
     * @param configurer the ContentNegotiationConfigurer object to configure
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(org.springframework.http.MediaType.APPLICATION_JSON);
    }

    /**
     * CORS configuration bean to allow cross-origin requests.
     * This bean configures the allowed origins, methods, and headers for CORS requests.
     * @return CorsConfigurationSource instance
     */
    @Bean
    public static CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(ALLOWED_ORIGINS));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Configures CSRF protection for the application.
     * This method disables CSRF protection in development mode and ignores CSRF for public URLs in production mode.
     * @param http the HttpSecurity object to configure
     * @throws Exception if an error occurs during configuration
     */
    public static void configureCSRF(HttpSecurity http) throws Exception {
        if (ENVIRONMENT != null && ENVIRONMENT.equals("development")) {
            http.csrf(AbstractHttpConfigurer::disable);
        } else {
            http.csrf(csrf -> csrf.ignoringRequestMatchers(PUBLIC_URLS));
        }
    }
}
