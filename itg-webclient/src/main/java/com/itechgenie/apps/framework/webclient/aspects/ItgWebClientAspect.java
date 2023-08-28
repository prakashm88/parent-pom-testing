package com.itechgenie.apps.framework.webclient.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@Slf4j
public class ItgWebClientAspect implements Ordered {

	@Around("within(com.itechgenie.apps.framework.**)")
	public Object aroundExecutionForFakeDataServiceImpl(ProceedingJoinPoint joinPoint) throws Throwable {
		long start = System.currentTimeMillis();
		// Before method execution
		log.info("Before execution of: " + joinPoint.getSignature());

		// Proceed with the method execution
		Object result = joinPoint.proceed();

		long executionTime = System.currentTimeMillis() - start;

		// After method execution
		log.info("After execution of: " + joinPoint.getSignature());
		log.info("Inside ItgWebClientAspect.aroundExecutionForFakeDataServiceImpl: " + joinPoint.getSignature()
				+ " executed in " + executionTime + "ms");

		return result;
	}

	@Around("within(com.itechgenie.apps.framework.webclient.services.ItgWebClientImpl)")
	public Object aroundExecutionForItgWebClientImpl(ProceedingJoinPoint joinPoint) throws Throwable {
		long start = System.currentTimeMillis();
		// Before method execution
		log.info("Before execution of: " + joinPoint.getSignature());

		// Proceed with the method execution
		Object result = joinPoint.proceed();

		long executionTime = System.currentTimeMillis() - start;

		// After method execution
		log.info("After execution of: " + joinPoint.getSignature());
		log.info("Inside ItgWebClientAspect.aroundExecutionForItgWebClientImpl: " + joinPoint.getSignature()
				+ " executed in " + executionTime + "ms");

		return result;
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	@Pointcut("@annotation(ItgWebClient)")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

		long start = System.currentTimeMillis();

		Object proceed = joinPoint.proceed();

		long executionTime = System.currentTimeMillis() - start;

		log.info("Inside ItgWebClientAspect.invokeProxy: " + joinPoint.getSignature() + " executed in " + executionTime
				+ "ms");
		return proceed;
	}

}
