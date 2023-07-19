package com.itechgenie.apps.framework.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

//import com.itechgenie.apps.jdk11.sb3.configs.AppAnnotationRegistrar;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
//@Import(AppAnnotationRegistrar.class)
public @interface EnableItgWebClients {
	String value();

	Class<?>[] client();
}