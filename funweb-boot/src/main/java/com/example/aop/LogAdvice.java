package com.example.aop;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import lombok.extern.java.Log;

@Aspect //횡단관점 
@Component
@Log
public class LogAdvice { //여러 주요 로직에 적용될 보조기능 클래스
	
	@Before("execution(public * com.example.service.SampleService.*(..))")
	public void logBefore() {
		log.info("==============================");
	}
	
	@Before("execution(* com.example.service.SampleService.doAdd(String,String)) && args(str1,str2)") // type이랑 매개변수 이름 같이 적윽 수 있음 매개변슨 args()
	public void logBeforeWriteParam(String str1,String str2) {
		log.info("str1 : " + str1);
		log.info("str2 : " + str2);
		}
	
	@AfterThrowing(pointcut = "execution ( * com.example.service.SampleService.*(..))", throwing = "exception")
	public void logException(Exception exception) {
		log.info("Exception...@@@");
		log.info("exception : " + exception);
	}
	
	
	@Around("execution(* com.example.service.SampleService.*(..))")
	public Object logTime(ProceedingJoinPoint pjp) {
		
		long startTime = System.currentTimeMillis();
		
		log.info("Target: "+ pjp.getTarget());
		log.info("Param : " +Arrays.toString(pjp.getArgs()) );
		Object result = null;

		try {
			result = pjp.proceed(); //타켓으로 삼은 매소드를 호출 -- 여기서는 doAdd()
		} catch (Throwable e) {
//			e.printStackTrace();
			log.info("excetion has occurred");
		}
		
		long endTime = System.currentTimeMillis();
		log.info("method run time : "+(endTime-startTime)+"ms");

		return result;
	}
}
