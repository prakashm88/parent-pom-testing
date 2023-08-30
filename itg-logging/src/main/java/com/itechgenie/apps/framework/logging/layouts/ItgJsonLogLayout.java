package com.itechgenie.apps.framework.logging.layouts;

import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MultiValueMap;

import com.itechgenie.apps.framework.core.security.dtos.CustomUserDetails;
import com.itechgenie.apps.framework.core.utils.AppCommonUtil;
import com.nimbusds.jose.shaded.gson.JsonObject;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.LayoutBase;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class ItgJsonLogLayout extends LayoutBase<ILoggingEvent> {

	/*
	 * private final ItgLogMessageProcessor logMessageProcessor;
	 * 
	 * public ItgJsonLogLayout(ItgLogMessageProcessor logMessageProcessor) {
	 * this.logMessageProcessor = logMessageProcessor; }
	 */

	@Override
	public String doLayout(ILoggingEvent event) {

		// Create a JSON object with the desired key-value pairs
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("timestamp", event.getTimeStamp());
		jsonObject.addProperty("level", event.getLevel().toString());
		jsonObject.addProperty("message", event.getFormattedMessage());
		jsonObject.addProperty("itgid", MDC.get("itgRequestId"));
		jsonObject.addProperty("traceId", MDC.get("traceId")); // Get traceId from MDC
		jsonObject.addProperty("spanId", MDC.get("spanId")); // Get spanId from MDC

		processLogMessage(jsonObject, event);

		return jsonObject.toString() + System.lineSeparator();
	}

	public void processLogMessage(JsonObject jsonObject, ILoggingEvent event) {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			String username = authentication.getName();
			jsonObject.addProperty("username", username);
			jsonObject.addProperty("auth-jwt", String.valueOf(authentication.getCredentials()));

			log.info("Auth object: " + AppCommonUtil.toJson(authentication));

			try {
				CustomUserDetails userDetail = (CustomUserDetails) authentication.getPrincipal();
				MultiValueMap<String, Object> headers = userDetail.getRequestHeaders();

				jsonObject.addProperty("itgRequestId", String.valueOf(headers.get("itgRequestId")));

			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("Exception: " + e.getMessage());
			}

		}

	}

}
