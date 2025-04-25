package com.example.assignment.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BaseAspect {
    /**
     * Logger instance for logging information, messages, and exceptions
     *  This attribute is protected to allow subclasses to access it
     */
    protected final Logger logger;

    protected BaseAspect(Logger logger) {
        this.logger = logger;
    }

    /**
     * Get class name and method name from the join point
     * @param joinPoint the join point
     * @return a map containing class name and method name
     */
    protected Map<String, String> getInformation(JoinPoint joinPoint) {
        // Extract class name, method name
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        String className = joinPoint.getTarget().getClass().getName();
        return Map.of("class", className, "method", methodName);
    }

    /**
     * Log class name, method name, and method arguments
     * @param joinPoint - the join point
     */
    protected void logInformation(JoinPoint joinPoint) {
        // Extract class name, method name, and permission
        Map<String, String> information = getInformation(joinPoint);
        String className = information.get("class");
        String methodName = information.get("method");
        // Get method arguments
        Object[] args = joinPoint.getArgs();

        if (logger.isLoggable(Level.INFO)) {
            logger.info(String.format("Class: %s, Method: %s", className, methodName));
            Arrays.stream(args).forEach(arg -> logger.info(String.format("Method arguments: %s", arg)));
        }
    }

    /**
     * Log a message with a specific level
     * @param message the message to log (try to build the message with String.format or StringBuilder/StringBuffer)
     * @param level the level of the message
     */
    protected void logMsg(String message, Level level) {
        if (logger.isLoggable(level)) {
            logger.log(level, message);
        }
    }

    /**
     * Log an exception
     * @param joinPoint the join point
     * @param throwable the exception to log
     */
    protected void logEx(JoinPoint joinPoint, Throwable throwable) {
        Map<String, String> information = getInformation(joinPoint);
        String className = information.get("class");
        String methodName = information.get("method");

        if (logger.isLoggable(Level.SEVERE)) {
            logger.severe(String.format("Class: %s, Method: %s, Message: %s, Cause: %s",
                className,
                methodName,
                throwable.getMessage(),
                (throwable.getCause() != null) ? throwable.getCause().getMessage() : "UNKNOWN"));
        }
    }
}
