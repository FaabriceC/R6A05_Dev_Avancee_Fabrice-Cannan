package com.master.air.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(ServiceLoggingAspect.class);

    @Around("execution(* com.master.air.service..*(..))")
    public Object logService(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();

        MethodSignature sig = (MethodSignature) pjp.getSignature();
        String method = sig.getDeclaringType().getSimpleName() + "." + sig.getName();

        log.info("-> {}", method);

        try {
            Object result = pjp.proceed();
            long ms = System.currentTimeMillis() - start;
            log.info("<- {} ({} ms)", method, ms);
            return result;
        } catch (Throwable ex) {
            long ms = System.currentTimeMillis() - start;
            log.warn("!! {} failed ({} ms): {}: {}", method, ms, ex.getClass().getSimpleName(), ex.getMessage());
            throw ex;
        }
    }
}