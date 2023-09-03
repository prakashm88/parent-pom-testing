package com.itechgenie.apps.framework.logging.layouts;

import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MultiValueMap;

import com.itechgenie.apps.framework.core.security.dtos.CustomUserDetails;
import com.itechgenie.apps.framework.core.utils.AppCommonUtil;
import com.itechgenie.apps.framework.logging.dtos.MDCLogEventMap;
import com.itechgenie.apps.framework.logging.dtos.MDCLogEventMap2;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.LayoutBase;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class ItgJsonLogLayout2 extends LayoutBase<ILoggingEvent> {

	/*
	 * private final ItgLogMessageProcessor logMessageProcessor;
	 * 
	 * public ItgJsonLogLayout(ItgLogMessageProcessor logMessageProcessor) {
	 * this.logMessageProcessor = logMessageProcessor; }
	 */

	@Override
	public String doLayout(ILoggingEvent event) {

		MDCLogEventMap2 jsonObject = new MDCLogEventMap2();
		// Create a JSON object with the desired key-value pairs
		jsonObject.put("timestamp", event.getTimeStamp());
		jsonObject.put("level", event.getLevel().toString());
		
		jsonObject.put("itgid", MDC.get("itgRequestId"));
		jsonObject.put("traceId", MDC.get("traceId")); // Get traceId from MDC
		jsonObject.put("spanId", MDC.get("spanId")); // Get spanId from MDC
		
		// Add class name, method name, and line number
	    StackTraceElement[] callerData = event.getCallerData();
	    if (callerData != null && callerData.length > 0) {
	        StackTraceElement caller = callerData[0];
	        jsonObject.put("className", caller.getClassName());
	        jsonObject.put("methodName", caller.getMethodName());
	        jsonObject.put("lineNo", caller.getLineNumber());
	    }

	    jsonObject.put("thread", event.getThreadName()); // Get thread name

		processLogMessage(jsonObject, event);

		jsonObject.put("message", event.getFormattedMessage());
		return AppCommonUtil.toJson(jsonObject) + System.lineSeparator();
	}

	public void processLogMessage(MDCLogEventMap2 jsonObject, ILoggingEvent event) {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			String username = authentication.getName();
			jsonObject.put("username", username);
			jsonObject.put("auth-jwt", String.valueOf(authentication.getCredentials()));

			log.info("Auth object: " + AppCommonUtil.toJson(authentication));

			try {
				CustomUserDetails userDetail = (CustomUserDetails) authentication.getPrincipal();
				MultiValueMap<String, Object> headers = userDetail.getRequestHeaders();

				jsonObject.put("itgRequestId", String.valueOf(headers.get("itgRequestId")));

			} catch (Exception e) {
				log.error("Exception: " + e.getMessage());
			}

		}

	}

}
