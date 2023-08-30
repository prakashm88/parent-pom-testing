package com.itechgenie.apps.framework.security.configs.nrx;

import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.itechgenie.apps.framework.core.security.dtos.CustomUserDetails;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomNonReactiveUserDetailsService implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub

		log.debug("Inside CustomReactiveUserDetailsService.findByUsername: user id: " + username);

		CustomUserDetails ud = new CustomUserDetails();
		ud.setUsername(username);
		ud.setSessionId("ITG".concat(UUID.randomUUID().toString()));
		ud.setClientId("ITG-WEB");

		return ud;
	}

}
