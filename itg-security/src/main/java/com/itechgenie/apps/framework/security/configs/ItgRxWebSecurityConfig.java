package com.itechgenie.apps.framework.security.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManagerResolver;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ServerWebExchange;

import com.itechgenie.apps.framework.security.configs.rx.CustomReactiveAuthenticationManager;
import com.itechgenie.apps.framework.security.configs.rx.CustomReactiveUserDetailsService;
import com.itechgenie.apps.framework.security.configs.rx.provider.CustomReactiveAuthenticationProvider;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
//@ConditionalOnClass(name = "org.springframework.web.reactive.config.EnableWebFluxSecurity")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ItgRxWebSecurityConfig {

	@Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
	String jwkUri;

	@PostConstruct
	void postConstruct() {
		log.info("Inside post constrct of ItgRxWebSecurityConfig - A Reactive Security config !");
	}

	@Bean
	SecurityWebFilterChain configure(ServerHttpSecurity http) throws Exception {

		http.authorizeExchange(
				(exchange) -> exchange.pathMatchers("/", "/public/**", "/**login**", "/error", "/webjars/**",
						"/actuator/**", "/v3/api-docs/**", "swagger-ui.html").permitAll().anyExchange().authenticated())
				.oauth2ResourceServer((oauth2ResourceServer) -> {
					oauth2ResourceServer.authenticationManagerResolver(resolver());
				});

		return http.build();
	}

	public ReactiveAuthenticationManagerResolver<ServerWebExchange> resolver() {
		return exchange -> {
			if (exchange.getRequest().getPath().subPath(0).value().startsWith("/employee")) {
				log.debug("Resolving employee path");
				return Mono.just(customReactiveAuthenticationManager());
			}
			log.debug("Resolving normal path");
			return Mono.just(customReactiveAuthenticationManager());
		};
	}

	@Bean
	CustomReactiveUserDetailsService customReactiveUserDetailsService() {
		return new CustomReactiveUserDetailsService();
	}

	@Bean
	CustomReactiveAuthenticationManager customReactiveAuthenticationManager() {
		return new CustomReactiveAuthenticationManager(customReactiveUserDetailsService(), jwkUri);
	}
	
	@Bean
	CustomReactiveAuthenticationProvider CustomReactiveAuthenticationProvider() {
		return new CustomReactiveAuthenticationProvider();
	}

}
