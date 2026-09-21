package dynamicUi.demo.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Aspect
@Component
public class ServiceLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(ServiceLoggingAspect.class);

    @Around("execution(* dynamicUi.demo.service..*.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {

        boolean generatedOwnId = MDC.get("requestId") == null;
        if (generatedOwnId) {
            MDC.put("requestId", UUID.randomUUID().toString().substring(0, 8));
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String serviceMethod = signature.getDeclaringType().getSimpleName() + "#" + signature.getName();

        log.info(" -> {} args={}", serviceMethod, AopLoggingUtils.redactedArgs(joinPoint.getArgs()));

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - start;
            log.info("<- {} completed in {}ms", serviceMethod, elapsed);
            return result;
        } catch (ResponseStatusException rse) {
            long elapsed = System.currentTimeMillis() - start;

            if (rse.getStatusCode().is4xxClientError()) {
                // Expected business-rule rejection — not a bug, just noisy at ERROR level
                log.warn("  <x {} rejected after {}ms :: {} {}",
                        serviceMethod, elapsed, rse.getStatusCode(), rse.getReason());
            } else {
                log.error("  <x {} failed after {}ms :: {} {}",
                        serviceMethod, elapsed, rse.getStatusCode(), rse.getReason());
            }
            throw rse;

        } catch (Throwable ex) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("  <x {} failed after {}ms :: {}: {}",
                    serviceMethod, elapsed, ex.getClass().getSimpleName(), ex.getMessage());
            throw ex;

        } finally {
            if (generatedOwnId) {
                MDC.remove("requestId");
            }
        }
    }
}
