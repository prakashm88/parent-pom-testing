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

	@Override
	public String doLayout(ILoggingEvent event) {

		MDCLogEventMap jsonObject = new MDCLogEventMap();
		// Create a JSON object with the desired key-value pairs
		jsonObject.setTimestamp(event.getTimeStamp());
		jsonObject.setLevel(event.getLevel().toString());

		jsonObject.setItgid(MDC.get("itgRequestId"));
		jsonObject.setItgRequestId(MDC.get("itgRequestId"));
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

		handleSecurityContextData(jsonObject, event);

		jsonObject.setMessage(event.getFormattedMessage());
		return AppCommonUtil.toJson(jsonObject) + System.lineSeparator();
	}

	public void handleSecurityContextData(MDCLogEventMap jsonObject, ILoggingEvent event) {

		try {
			// Attempt to fetch the security context reactively
			Mono<SecurityContext> securityContextMono = ReactiveSecurityContextHolder.getContext();
			securityContextMono.flatMap(securityContext -> {
				Authentication authentication = securityContext.getAuthentication();
				processAuthentication(jsonObject, authentication);
				return Mono.empty();
			}).subscribe();
		} catch (Exception e) {
			// If reactive fetch fails, try to fetch non-reactively
			SecurityContext securityContext = SecurityContextHolder.getContext();
			Authentication authentication = securityContext.getAuthentication();
			processAuthentication(jsonObject, authentication);
		}

	}

	private void processAuthentication(MDCLogEventMap jsonObject, Authentication authentication) {
		if (authentication != null && authentication.isAuthenticated()) {
			String username = authentication.getName();
			jsonObject.setUsername(username);
			jsonObject.setAuthjwt(String.valueOf(authentication.getCredentials()));

			log.info("Auth object: " + AppCommonUtil.toJson(authentication));

			try {
				if (authentication.getPrincipal() instanceof CustomUserDetails) {
					CustomUserDetails userDetail = (CustomUserDetails) authentication.getPrincipal();
					MultiValueMap<String, Object> headers = userDetail.getRequestHeaders();
					jsonObject.setItgRequestId(String.valueOf(headers.get("Authorization")));
				} else {

				}
			} catch (Exception e) {
				log.error("Exception: " + e.getMessage());
			}
		} else {
			jsonObject.setUsername("NotFound");
			jsonObject.setAuthjwt("NotFound");
		}
	}

}
