package com.itechgenie.apps.framework.webclient.config;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.itechgenie.apps.framework.webclient.annotations.ItgWebClient;
import com.itechgenie.apps.framework.webclient.services.ItgWebClientImpl;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AppWebClientRegistrar implements BeanPostProcessor, Ordered {

	@Autowired
	private ItgWebClientImpl itgWebClientImpl;

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		// log.debug("Inside postProcessBeforeInitialization - beanName: + " +
		// beanName);
		ReflectionUtils.doWithFields(bean.getClass(), field -> processField(field, bean));
		return bean;
	}

	private void processField(Field field, Object bean) {
		ItgWebClient annotation = AnnotationUtils.getAnnotation(field.getType(), ItgWebClient.class);
		if (annotation != null) {
			Object proxy = null;
			Class<?> fieldType = field.getType();
			if (fieldType.isInterface()) {
				proxy = Proxy.newProxyInstance(fieldType.getClassLoader(), new Class[] { fieldType },
						(proxyObj, method, args) -> {
							log.info("Inside AppAnnotationRegistrar.processField: - method: " + method + " - args: "
									+ args);
							// Retrieve annotation attributes
							String id = annotation.id();
							return itgWebClientImpl.execute(id, fieldType.getName(), method, annotation, args);
						});
			} else {
				log.info("Not an interface - this cannot happen !!!!");
				proxy = Enhancer.create(fieldType, new MethodInterceptor() {
					@Override
					public Object intercept(Object proxyObj, Method method, Object[] args, MethodProxy methodProxy)
							throws Throwable {
						log.info("Inside ItgWebClientRegistrarV3.processField: - method: " + method + " - args: "
								+ args);
						// Retrieve annotation attributes
						String id = annotation.id();
						String url = annotation.url();
						String[] headers = annotation.headers();

						log.debug("Inside AppAnnotationRegistrar: id:" + id + " - url: " + url + " - headers: "
								+ headers);

						return itgWebClientImpl.execute(id, fieldType.getName(), method, annotation, true, args);
					}
				});
			}
			ReflectionUtils.setField(field, bean, proxy);
		}
	}

}
