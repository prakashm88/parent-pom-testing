package com.itechgenie.apps.framework.security.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.itechgenie.apps.framework.security.configs.nrx.CustomNonReactiveAuthenticationManager;
import com.itechgenie.apps.framework.security.configs.nrx.CustomNonReactiveUserDetailsService;
import com.itechgenie.apps.framework.security.configs.nrx.provider.CustomNonReactiveAuthenticationProvider;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
//@ConditionalOnClass(name = "org.springframework.security.config.annotation.web.configuration.EnableWebSecurity")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ItgNonRxWebSecurityConfig {

	@Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
	String jwkUri;

	@PostConstruct
	void postConstruct() {
		log.info("Inside post constrct of ItgNonRxWebSecurityConfig - A Servlet based Security config !");
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(
				requests -> requests.requestMatchers("/", "/public/**", "/**login**", "/error", "/webjars/**",
						"/actuator/**", "/v3/api-docs/**", "swagger-ui.html").permitAll().anyRequest().authenticated())
				.oauth2ResourceServer((oauth2ResourceServer) -> {
					oauth2ResourceServer.authenticationManagerResolver(resolver());
				})

				.httpBasic(Customizer.withDefaults());
		return http.build();
	}

	@Bean
	AuthenticationManagerResolver<HttpServletRequest> resolver() {
		return request -> {
			if (request.getRequestURI().startsWith("/")) {
				return customNonReactiveAuthenticationManager();
			}
			// Return a different authentication manager if needed
			return null;
		};
	}

	@Bean
	CustomNonReactiveUserDetailsService customNonReactiveUserDetailsService() {
		return new CustomNonReactiveUserDetailsService();
	}

	@Bean
	CustomNonReactiveAuthenticationProvider customNonReactiveAuthenticationProvider() {
		return new CustomNonReactiveAuthenticationProvider();
	}

	@Bean
	CustomNonReactiveAuthenticationManager customNonReactiveAuthenticationManager() {
		return new CustomNonReactiveAuthenticationManager(customNonReactiveUserDetailsService(), this.jwkUri);
	}
}
