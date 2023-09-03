package com.itechgenie.apps.framework.logging.layouts;

import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MultiValueMap;

import com.itechgenie.apps.framework.core.security.dtos.CustomUserDetails;
import com.itechgenie.apps.framework.core.utils.AppCommonUtil;
import com.itechgenie.apps.framework.logging.dtos.MDCLogEventMap;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.LayoutBase;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

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

		MDCLogEventMap jsonObject = new MDCLogEventMap();
		// Create a JSON object with the desired key-value pairs
		jsonObject.setTimestamp(event.getTimeStamp());
		jsonObject.setLevel(event.getLevel().toString());

		jsonObject.setItgid(MDC.get("itgRequestId"));
		jsonObject.setTraceId(MDC.get("traceId")); // Get traceId from MDC
		jsonObject.setSpanId(MDC.get("spanId")); // Get spanId from MDC

		// Add class name, method name, and line number
		StackTraceElement[] callerData = event.getCallerData();
		if (callerData != null && callerData.length > 0) {
			StackTraceElement caller = callerData[0];
			jsonObject.setClassName(caller.getClassName());
			jsonObject.setMethodName(caller.getMethodName());
			jsonObject.setLineNo(caller.getLineNumber());
		}

		jsonObject.setThread(event.getThreadName()); // Get thread name

		processLogMessage(jsonObject, event);

		jsonObject.setMessage(event.getFormattedMessage());
		return AppCommonUtil.toJson(jsonObject) + System.lineSeparator();
	}

	public void processLogMessage(MDCLogEventMap jsonObject, ILoggingEvent event) {
		ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication)
				.flatMap(authentication -> processAuthentication(jsonObject, authentication)).then();
	}

	private Mono<Void> processAuthentication(MDCLogEventMap jsonObject, Authentication authentication) {
		String username = authentication.getName();
		jsonObject.setUsername(username);
		jsonObject.setAuthjwt(String.valueOf(authentication.getCredentials()));

		log.info("Auth object: " + AppCommonUtil.toJson(authentication));

		try {
			CustomUserDetails userDetail = (CustomUserDetails) authentication.getPrincipal();
			MultiValueMap<String, Object> headers = userDetail.getRequestHeaders();
			jsonObject.setItgRequestId(String.valueOf(headers.get("itgRequestId")));
		} catch (Exception e) {
			log.error("Exception: " + e.getMessage());
		}

		return Mono.empty();
	}

	public void _processLogMessage(MDCLogEventMap jsonObject, ILoggingEvent event) {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			String username = authentication.getName();
			jsonObject.setUsername(username);
			jsonObject.setAuthjwt(String.valueOf(authentication.getCredentials()));

			log.info("Auth object: " + AppCommonUtil.toJson(authentication));

			try {
				CustomUserDetails userDetail = (CustomUserDetails) authentication.getPrincipal();
				MultiValueMap<String, Object> headers = userDetail.getRequestHeaders();

				jsonObject.setItgRequestId(String.valueOf(headers.get("itgRequestId")));

			} catch (Exception e) {
				log.error("Exception: " + e.getMessage());
			}

		}

	}

}
