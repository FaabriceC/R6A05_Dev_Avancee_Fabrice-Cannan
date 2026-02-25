package com.master.air.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.master.air.service..*(..))")
    public Object logServiceMethods(ProceedingJoinPoint jp) throws Throwable {
        String method = jp.getSignature().toShortString();
        log.debug(">>> {}", method);
        long start = System.currentTimeMillis();
        try {
            Object result = jp.proceed();
            log.debug("<<< {} ({}ms)", method, System.currentTimeMillis() - start);
            return result;
        } catch (Exception e) {
            log.error("!!! {} ({}ms) - {}: {}", method, System.currentTimeMillis() - start,
                    e.getClass().getSimpleName(), e.getMessage());
            throw e;
        }
    }
}
