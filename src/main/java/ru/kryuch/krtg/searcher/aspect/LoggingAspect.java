package ru.kryuch.krtg.searcher.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* ru.kryuch.krtg.searcher.integration.tg.TelegramPythonClient.*(..))")
    public Object logTelegramClient(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info("{}.{} (request = {})", className, methodName, args);

        try {
            Object result = joinPoint.proceed();
            log.info("{}.{} (response = {})", className, methodName, result);
            return result;
        } catch (Exception e) {
            log.error("{}.{} failed: {}", className, methodName, e.getMessage(), e);
            throw e;
        }
    }
}