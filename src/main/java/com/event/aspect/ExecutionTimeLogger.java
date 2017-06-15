package com.event.aspect;


import com.event.entities.Event;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExecutionTimeLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionTimeLogger.class);

    @Before("execution(* com.event.services.EventService.createEvent(com.event.entities.Event)) && args(event)")
    public void beforeSampleCreation(Event event) {

        LOGGER.info("A create request was issued for : "+ event);
    }

//    @Around("execution(* com.event.services.EventService(..))")
    @Around("execution(* com.event.services.EventService.createEvent(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        final long start = System.currentTimeMillis();

        final Object proceed = joinPoint.proceed();

        final long executionTime = System.currentTimeMillis() - start;

        LOGGER.info(joinPoint.getSignature() + " executed in " + executionTime + "ms");

        return proceed;
    }

}
