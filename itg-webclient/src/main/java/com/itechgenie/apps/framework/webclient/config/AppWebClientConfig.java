package com.itechgenie.apps.framework.webclient.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "itg.webclient" )
@Data
public class AppWebClientConfig {

	private boolean debug = false;

}
