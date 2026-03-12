package ru.sshibko.backend_seblog.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
@Slf4j
public class RequestIdInterceptor implements HandlerInterceptor {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String MDC_REQUEST_ID_KEY = "requestId";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        String requestId = getOrGenerateRequestId(request);

        MDC.put(MDC_REQUEST_ID_KEY, requestId);
        response.setHeader(REQUEST_ID_HEADER, requestId);
        log.info("Incoming request: {} {} [requestId: {}]",
                request.getMethod(), request.getRequestURI(), requestId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {

        String requestId = MDC.get(MDC_REQUEST_ID_KEY);
        log.info("Request completed: {} {} -> {} [requestId: {}]",
                request.getMethod(), request.getRequestURI(),
                response.getStatus(), requestId);

        MDC.remove(MDC_REQUEST_ID_KEY);
    }

    private String getOrGenerateRequestId(HttpServletRequest request) {
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = "req-" + UUID.randomUUID().toString().substring(0, 8);
        }
        return requestId;
    }
}
