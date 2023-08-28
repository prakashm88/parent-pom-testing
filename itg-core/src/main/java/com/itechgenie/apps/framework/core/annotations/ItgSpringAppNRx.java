package com.itechgenie.apps.framework.core.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Component;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
@Documented
@SpringBootApplication
@EnableWebSecurity
@ComponentScan(basePackages = { "com.itechgenie.apps.framework", "com.itechgenie.apps.micros" })
public @interface ItgSpringAppNRx {

}
