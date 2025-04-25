package com.example.assignment.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.CompositeConverter;

/**
 * HighlightingCompositeConverterEx is a custom logback converter that highlights the log messages based on the log level.
 * It uses ANSI escape codes for color.
 * the console output must support ANSI escape codes for this to work.
 * References the original code from the original repository: <br/>
 * <a href="https://github.com/shuwada/logback-custom-color">https://github.com/shuwada/logback-custom-color</a>
 */
public class HighlightingCompositeConverterEx extends CompositeConverter<ILoggingEvent> {

    // using ANSI escape codes for color
    @Override
    protected String transform(ILoggingEvent event, String in) {
        Level level = event.getLevel();
        String colorCode = switch (level.toInt()) {
            case Level.ERROR_INT -> "\u001B[1;31m"; // Bold Red
            case Level.WARN_INT -> "\u001B[33m"; // Yellow
            case Level.INFO_INT -> "\u001B[34m"; // Blue
            case Level.DEBUG_INT -> "\u001B[32m"; // Green
            default -> "\u001B[0m"; // Default
        };
        return colorCode + in + "\u001B[0m"; // Reset color
    }
}