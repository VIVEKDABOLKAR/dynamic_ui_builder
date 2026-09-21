package dynamicUi.demo.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.UUID;

import static dynamicUi.demo.aspect.AopLoggingUtils.redactedArgs;

@Aspect
@Component
public class RequestLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingAspect.class);

    @Around("execution(* dynamicUi.demo.controller..*.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {

        String requestId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("requestId", requestId); // correlates every log line for this call, incl. inside services

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String controllerMethod = signature.getDeclaringType().getSimpleName() + "#" + signature.getName();
        String userName = currentUsername();
        String httpMethod = "?";
        String uri = "?";
        HttpServletRequest request = currentHttpRequest();
        if (request != null) {
            httpMethod = request.getMethod();
            uri = request.getRequestURI();
        }

        log.info("--> [{}] {} {} :: {} args={}",
                requestId, httpMethod, uri, controllerMethod, redactedArgs(joinPoint.getArgs()));

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - start;

            log.info("<-- [{}] user=[{}] {} {} :: {} completed in {}ms",
                    requestId, userName,httpMethod, uri, controllerMethod, elapsed);

            return result;

        } catch (Throwable ex) {
            long elapsed = System.currentTimeMillis() - start;

            log.error("<-x [{}] user=[{}] {} {} :: {} failed after {}ms :: {}: {}",
                    requestId,userName, httpMethod, uri, controllerMethod, elapsed,
                    ex.getClass().getSimpleName(), ex.getMessage());

            throw ex;

        } finally {
            MDC.remove("requestId");
        }
    }

    private HttpServletRequest currentHttpRequest() {
        var attrs = RequestContextHolder.getRequestAttributes();
        return attrs instanceof ServletRequestAttributes servletAttrs ? servletAttrs.getRequest() : null;
    }

    private String currentUsername() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {
            return "anonymous";
        }

        return authentication.getName();
    }

}