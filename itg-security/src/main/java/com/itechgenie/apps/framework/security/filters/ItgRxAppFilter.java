package com.itechgenie.apps.framework.security.filters;

import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import io.micrometer.tracing.ScopedSpan;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ItgRxAppFilter implements WebFilter {

	@PostConstruct
	public void initMdc() {
		Hooks.enableAutomaticContextPropagation();
		log.info("Initaited the MDC in ItgRxAppFilter!");
	}

	@Autowired
	private Tracer tracer;

	@Autowired
	private ObservationRegistry observationRegistry;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

		ServerHttpRequest req = exchange.getRequest();

		String requestId = generateRequestId();
		ScopedSpan span = null;
		final Observation observation = Observation.start(requestId, observationRegistry);
		try {
			// observation = Observation.start(requestId, observationRegistry);
			span = tracer.startScopedSpan(requestId); // currentSpan().context().traceId();
			log.info("Starting a transaction for req : {}", req.getPath());

			// span = tracer.nextSpan().name(requestId).start();
			// Set up MDC context with request-specific information
			MDC.put("itgRequestId", requestId);
			// MDC.put("traceId", traceId);
			// MDC.put("spanId", span.context().spanId());
			exchange.mutate().request(exchange.getRequest().mutate().header("itgRequestId", requestId).build());
			// sexchange.getRequest().getHeaders().add("itgRequestId", id);

			log.info("Committing a transaction for req : {}", req.getPath());
		} catch (Exception e) {
			log.error("Exception occured in ItgRxAppFilter.filter: " + e.getMessage());
		} finally {
			span.end();
			observation.stop();
		}

		return chain.filter(exchange).contextWrite(Context.of("itg_headers", req.getHeaders()))
				.contextWrite(context -> context.put(ObservationThreadLocalAccessor.KEY, observation));
	}

	private String generateRequestId() {
		return UUID.randomUUID().toString();
	}

}