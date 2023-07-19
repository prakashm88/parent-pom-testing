package com.itechgenie.apps.framework.core.annotations;

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

	@Around("within(com.itechgenie.apps.jdk11.sb3.services.impl.FakeDataServiceImpl)")
	public Object aroundExecutionForFakeDataServiceImpl(ProceedingJoinPoint joinPoint) throws Throwable {
		long start = System.currentTimeMillis();
		// Before method execution
		log.info("Before execution of: " + joinPoint.getSignature());

		// Proceed with the method execution
		Object result = joinPoint.proceed();

		long executionTime = System.currentTimeMillis() - start;

		// After method execution
		log.info("After execution of: " + joinPoint.getSignature());
		log.info("Inside ItgWebClientAspect.aroundExecutionForFakeDataServiceImpl: " + joinPoint.getSignature() + " executed in " + executionTime
				+ "ms");

		return result;
	}
	
	@Around("within(com.itechgenie.apps.jdk11.sb3.services.impl.ItgWebClientImpl)")
	public Object aroundExecutionForItgWebClientImpl(ProceedingJoinPoint joinPoint) throws Throwable {
		long start = System.currentTimeMillis();
		// Before method execution
		log.info("Before execution of: " + joinPoint.getSignature());

		// Proceed with the method execution
		Object result = joinPoint.proceed();

		long executionTime = System.currentTimeMillis() - start;

		// After method execution
		log.info("After execution of: " + joinPoint.getSignature());
		log.info("Inside ItgWebClientAspect.aroundExecutionForItgWebClientImpl: " + joinPoint.getSignature() + " executed in " + executionTime
				+ "ms");

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

	/*
	 * //@Around("@within(ItgWebClient) && execution(* *(..))")
	 * 
	 * @Around("@target(itgWebClient) && execution(* *(..))") public Object
	 * invokeProxy(ProceedingJoinPoint joinPoint, ItgWebClient itgWebClient) throws
	 * Throwable { long start = System.currentTimeMillis();
	 * 
	 * Object proceed = joinPoint.proceed();
	 * 
	 * long executionTime = System.currentTimeMillis() - start;
	 * 
	 * log.info("Inside ItgWebClientAspect.invokeProxy: " + joinPoint.getSignature()
	 * + " executed in " + executionTime + "ms"); return proceed; }
	 */
	/*
	 * @Around("@annotation(ItgWebClient)") public Object
	 * logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable { long start
	 * = System.currentTimeMillis();
	 * 
	 * Object proceed = joinPoint.proceed();
	 * 
	 * long executionTime = System.currentTimeMillis() - start;
	 * 
	 * log.info("Inside ItgWebClientAspect.logExecutionTime: " +
	 * joinPoint.getSignature() + " executed in " + executionTime + "ms"); return
	 * proceed; }
	 */

}
