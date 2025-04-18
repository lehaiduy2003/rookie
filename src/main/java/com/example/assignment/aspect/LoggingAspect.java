package com.example.assignment.aspect;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * LoggingAspect class for logging information, messages, and exceptions
 */
@Component
@Aspect
public class LoggingAspect {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private Map<String, String> getInfo(JoinPoint joinPoint) {
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
    public void logInfo(JoinPoint joinPoint) {
        // Extract class name, method name, and permission
        Map<String, String> information = getInfo(joinPoint);
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
    public void logMsg(String message, Level level) {
        if (logger.isLoggable(level)) {
            logger.log(level, message);
        }
    }

    /**
     * Log an exception
     * @param joinPoint the join point
     * @param throwable the exception to log
     */
    public void logEx(JoinPoint joinPoint, Throwable throwable) {
        Map<String, String> information = getInfo(joinPoint);
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

    /**
     * Pointcut for methods annotated with @Logging
     */
    @Pointcut("@annotation(com.example.assignment.annotation.Logging)")
    public void loggingAnnotationPointcut() {
    }

    /**
     * Pointcut for classes annotated with @Logging
     */
    @Pointcut("@within(com.example.assignment.annotation.Logging)")
    public void loggingClassAnnotationPointcut() {
    }

    /**
     * Pointcut for all methods in classes ending with ServiceImpl
     */
    @Pointcut("execution(* com.example.assignment.service.impl..*ServiceImpl.*(..))")
    public void serviceImplPointcut() {
    }

    /**
     * Combined pointcut for all logging scenarios
     */
    @Pointcut("loggingAnnotationPointcut() || loggingClassAnnotationPointcut() || serviceImplPointcut()")
    public void loggingPointcut() {
    }

    /**
     * Around advice to log method entry and exit
     * @param joinPoint the join point
     * @return the result of the method execution
     * @throws Throwable if an error occurs during method execution
     */
    @Around("loggingPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // Log method entry
        logInfo(joinPoint);

        // Execute the method
        Object result;
        try {
            result = joinPoint.proceed();

            // Log method exit (success)
            if (logger.isLoggable(Level.INFO)) {
                Map<String, String> information = getInfo(joinPoint);
                String className = information.get("class");
                String methodName = information.get("method");
                String message = String.format("Class: %s, Method: %s, Result: %s", className, methodName, result);
                logMsg(message, Level.INFO);
            }

            return result;
        } catch (Throwable e) {
            // Exception will be logged by afterThrowing advice
            throw e;
        }
    }

    /**
     * After throwing advice to log exceptions
     * @param joinPoint the join point
     * @param exception the exception thrown
     */
    @AfterThrowing(pointcut = "loggingPointcut()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        logEx(joinPoint, exception);
    }
}
