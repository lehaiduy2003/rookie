package com.example.assignment.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Aspect
@Component
public class UserAspect extends BaseAspect {
    public UserAspect() {
        super(Logger.getLogger(UserAspect.class.getName()));
    }
}
