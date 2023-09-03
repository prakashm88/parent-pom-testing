package com.itechgenie.apps.framework.webclient.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
@Documented
@Lazy
public @interface ItgWebClient {
	String id() default "app-api-service";

	String url();

	boolean isBlocking() default false;

	String[] headers();

}
