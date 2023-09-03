package com.itechgenie.apps.framework.security.filters;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Hooks;

@Slf4j
@Component
@WebFilter(urlPatterns = "/*") // Apply the filter to all URLs
@Order(1) // Specify the order of the filter
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ItgWebAppFilter extends OncePerRequestFilter {

	@Autowired
	private Tracer tracer;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		log.info("ItgWebAppFilter.doFilterInternal :  Starting a transaction for req : {}", request.getRequestURI());
		Span span = null;
		try {

			String requestId = generateRequestId();
			span = tracer.nextSpan().name(requestId).start();

			// Set up MDC context with request-specific information
			MDC.put("itgRequestId", requestId);
			// request.addHeader("itgRequestId", id);
			HttpServletRequest requestWrapper = new CustomHeaderRequestWrapper(request, requestId);

			// Continue with the filter chain
			filterChain.doFilter(requestWrapper, response);
		} finally {
			// Clean up MDC context after the request is processed
			MDC.clear();
			span.end();
		}
	}

	private String generateRequestId() {
		return UUID.randomUUID().toString();
	}

	private static class CustomHeaderRequestWrapper extends HttpServletRequestWrapper {
		private final String requestId;

		public CustomHeaderRequestWrapper(HttpServletRequest request, String requestId) {
			super(request);
			this.requestId = requestId;
		}

		@Override
		public String getHeader(String name) {
			if ("itgRequestId".equalsIgnoreCase(name)) {
				return requestId;
			}
			return super.getHeader(name);
		}
	}

}
