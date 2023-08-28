package com.itechgenie.apps.framework.security.configs.nrx;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import com.itechgenie.apps.framework.core.utils.AppCommonUtil;
import com.itechgenie.apps.framework.security.dtos.CustomUserDetails;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomNonReactiveAuthenticationManager implements AuthenticationManager {

	private final CustomNonReactiveUserDetailsService userDetailsService;

	private final String jwkUrl;
	
	public CustomNonReactiveAuthenticationManager(CustomNonReactiveUserDetailsService userDetailsService, String jwkUrl) {
		this.userDetailsService = userDetailsService;
		this.jwkUrl = jwkUrl;
	}

	@Override
	public Authentication authenticate(Authentication authentication) {
		log.debug("Inside CustomReactiveAuthenticationManager.authenticate: " + authentication);

		JwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkUrl).build();

		Jwt token = jwtDecoder.decode(authentication.getName());

		log.debug(
				"Inside CustomReactiveAuthenticationManager.authenticate: JWT Object: " + AppCommonUtil.toJson(token));

		CustomUserDetails ud = (CustomUserDetails) validateAndFetchUserDetails(token);

		/*
		 * HttpHeaders headers = ctx.get("headers");
		 * 
		 * MultiValueMap<String, Object> requestHeaders = getHttpHeaderToMap(headers);
		 * 
		 * log.debug("$$$ Headers from parent : " + AppCommonUtil.toJson(headers));
		 * log.debug("$$$ Headers from context : " +
		 * AppCommonUtil.toJson(requestHeaders));
		 */

		// Create CustomUserDetails object with request headers
		ud.setClientId("UPDATED-ITG");
		//ud.setRequestHeaders(getAllHeaders()); -- add logic to update the headers here later 

		Authentication updatedAuthentication = new UsernamePasswordAuthenticationToken(ud, ud.getPassword(),
				ud.getAuthorities());

		log.debug("Inside CustomReactiveAuthenticationManager.updatedAuthentication: " + updatedAuthentication);

		return updatedAuthentication;

	}

	private UserDetails validateAndFetchUserDetails(Jwt token) {
		return userDetailsService.loadUserByUsername(token.getSubject());
	}

	
}
