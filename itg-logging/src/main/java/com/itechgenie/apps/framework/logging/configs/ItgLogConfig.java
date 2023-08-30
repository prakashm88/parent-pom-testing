package com.itechgenie.apps.framework.logging.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.itechgenie.apps.framework.logging.layouts.ItgJsonLogLayout;
import com.itechgenie.apps.framework.logging.processors.ItgLogMessageProcessor;

@Configuration
public class ItgLogConfig {

	@Bean
	ItgLogMessageProcessor logMessageProcessor() {
		return new ItgLogMessageProcessor();
	}

	@Bean
	ItgJsonLogLayout jsonLogLayout() {
		return new ItgJsonLogLayout();
	}

}
