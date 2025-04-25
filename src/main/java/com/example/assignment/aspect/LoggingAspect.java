package com.example.assignment.aspect;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * LoggingAspect class for logging information, messages, and exceptions
 */
@Component
@Aspect
public class LoggingAspect extends BaseAspect {

    public LoggingAspect() {
        super(Logger.getLogger(LoggingAspect.class.getName()));
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
        logInformation(joinPoint);

        // Execute the method
        Object result;
        try {
            result = joinPoint.proceed();

            // Log method exit (success)
            if (logger.isLoggable(Level.INFO)) {
                Map<String, String> information = getInformation(joinPoint);
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
