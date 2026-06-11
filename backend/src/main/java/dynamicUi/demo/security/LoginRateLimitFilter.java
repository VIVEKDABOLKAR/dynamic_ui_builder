package dynamicUi.demo.security;


import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket>  buckets = new ConcurrentHashMap<>();
    private final int capacity;
    private final int refillMinutes;

    public LoginRateLimitFilter(
            @Value("${app.rate-limit.login.capacity}") int capacity,
            @Value("${app.rate-limit.login.refill-minutes}") int refillMinutes

    ){
        this.capacity = capacity;
        this.refillMinutes = refillMinutes;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        if(!isLoginRequest(request)){
            chain.doFilter(request,response);
            return;
        }

        String ip = getClientIp(request);

        Bucket bucket = buckets.computeIfAbsent(ip,this::newBucket);

        // Try to consume 1 token — returns result + remaining count atomically
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            // Token consumed — pass request through, add remaining count header
            response.addHeader("X-Rate-Limit-Remaining",
                    String.valueOf(probe.getRemainingTokens()));
            chain.doFilter(request, response);
        } else {
            long waitSeconds = probe.getNanosToWaitForRefill() / 1_000_000_000;
            long waitMinutes = (long) Math.ceil(waitSeconds / 60.0);  // ← convert to minutes, round up
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitSeconds));
            response.getWriter().write(
                    "{\"error\":\"Too many login attempts. Please try again after "
                            + waitMinutes + " minute" + (waitMinutes > 1 ? "s" : "") + ".\"}"
            );
        }

    }



    private boolean isLoginRequest(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod())
                && "/api/auth/login".equals(request.getRequestURI());
    }

    private String getClientIp(HttpServletRequest request) {
        String  forwarded = request.getHeader("X-Forwarded-For");
        if(forwarded!=null && !forwarded.isBlank()){
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        System.out.println(request.getRemoteAddr());
        return request.getRemoteAddr();
    }

    private Bucket newBucket(String s) {

        Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillIntervally(capacity, Duration.ofMinutes(refillMinutes))
                .build();

        return Bucket.builder().addLimit(limit).build();
    }


}
