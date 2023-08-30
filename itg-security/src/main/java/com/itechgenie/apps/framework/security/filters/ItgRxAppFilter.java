package com.itechgenie.apps.framework.security.filters;

import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ItgRxAppFilter implements WebFilter {
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

		ServerHttpRequest req = exchange.getRequest();

		log.info("Starting a transaction for req : {}", req.getPath());

		String id = generateRequestId();
		// Set up MDC context with request-specific information
		MDC.put("itgRequestId", id);
		exchange.mutate().request( exchange.getRequest().mutate().header("itgRequestId", id).build());
		//sexchange.getRequest().getHeaders().add("itgRequestId", id);

		log.info("Committing a transaction for req : {}", req.getPath());
		return chain.filter(exchange).contextWrite(Context.of("headers", req.getHeaders()));
	}

	private String generateRequestId() {
		return UUID.randomUUID().toString();
	}

}