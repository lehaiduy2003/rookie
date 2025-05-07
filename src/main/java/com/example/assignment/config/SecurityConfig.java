package com.example.assignment.config;

import com.example.assignment.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.example.assignment.constant.SecurityURL.PUBLIC_URLS;

/**
 * Security configuration class to set up security filters for the application.
 * This class configures the security settings for the application using Spring Security.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final WebConfig webConfig;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthEntryPointImpl authEntryPointImpl;
    private final AccessDeniedHandlerImpl accessDeniedHandlerImpl;

    /**
     * Bean to configure the security filter chain.
     * This method sets up the security filters for the application.
     * It configures the CSRF protection, CORS settings, session management, and authorization rules.
     * It also adds the JWT authentication filter to the security filter chain.
     * @param http the HttpSecurity object to configure
     * @return a SecurityFilterChain instance
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        webConfig.configureCSRF(http); // Configure CSRF protection
        webConfig.configureCORS(http); // Configure CORS settings
        http
            // Use stateless session management
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Public endpoints - anyone can access
                .requestMatchers(HttpMethod.GET, PUBLIC_URLS).permitAll()
                .requestMatchers("/api/v1/auth/**").permitAll()
                // Any other request requires authentication
                .anyRequest().authenticated()
            );
        // Add JWT filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        // Set the authentication entry point
        http.exceptionHandling(exceptionHandling ->
            exceptionHandling
                .authenticationEntryPoint(authEntryPointImpl)
                .accessDeniedHandler(accessDeniedHandlerImpl)
        );

        return http.build();
    }
}
