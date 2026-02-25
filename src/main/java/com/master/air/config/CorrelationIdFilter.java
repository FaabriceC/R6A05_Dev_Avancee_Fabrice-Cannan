package com.master.air.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(1)
public class CorrelationIdFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            String cid = ((HttpServletRequest) request).getHeader("X-Correlation-ID");
            if (cid == null || cid.isBlank()) cid = UUID.randomUUID().toString().substring(0, 8);
            MDC.put("correlationId", cid);
            chain.doFilter(request, response);
        } finally {
            MDC.remove("correlationId");
        }
    }
}
