package com.itechgenie.apps.framework.logging.processors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MultiValueMap;

import com.itechgenie.apps.framework.core.security.dtos.CustomUserDetails;
import com.nimbusds.jose.shaded.gson.JsonObject;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class ItgLogMessageProcessor {

	public void processLogMessage(JsonObject jsonObject, ILoggingEvent event) {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			String username = authentication.getName();
			jsonObject.addProperty("username", username);
			CustomUserDetails userDetail = (CustomUserDetails) authentication.getPrincipal();
			MultiValueMap<String, Object> headers = userDetail.getRequestHeaders();

			jsonObject.addProperty("itgRequestId", String.valueOf(headers.get("itgRequestId")));
			jsonObject.addProperty("auth-jwt", String.valueOf(headers.get("authorization")));
		}

	}

}
