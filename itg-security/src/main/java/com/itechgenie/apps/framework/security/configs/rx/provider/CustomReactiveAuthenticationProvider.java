package com.itechgenie.apps.framework.security.configs.rx.provider;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.itechgenie.apps.framework.core.utils.AppCommonUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomReactiveAuthenticationProvider implements AuthenticationProvider {

	@Value("${spring.security.user.password}")
	private String appPassword;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		log.info("AuthObject: " + AppCommonUtil.toJson(authentication));

		String name = authentication.getName();
		String password = authentication.getCredentials().toString();

		if (validateUserNPassword(name, password)) {

			return new UsernamePasswordAuthenticationToken(name, password, new ArrayList<>());
		} else {
			return null;
		}

	}

	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

	private boolean validateUserNPassword(String username, String password) {
		if ("user".equalsIgnoreCase(username) && appPassword.equalsIgnoreCase(password)) {
			log.info("Auth validated : " + username);
			return true;
		}
		log.error("Invalid user auth : " + username);
		return false;
	}

}
