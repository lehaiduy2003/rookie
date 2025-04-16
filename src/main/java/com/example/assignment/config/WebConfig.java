package com.example.assignment.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration class to set up content negotiation.
 * This class configures the default content type for the application.
 * It implements the WebMvcConfigurer interface to customize the default settings.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        // Set the default content type to Application/JSON
        configurer.defaultContentType(org.springframework.http.MediaType.APPLICATION_JSON);
    }
}
