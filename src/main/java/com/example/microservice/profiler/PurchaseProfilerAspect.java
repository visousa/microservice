package com.example.microservice.profiler;

import com.codahale.metrics.Timer;
import com.example.microservice.metric.MetricsService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/**
 * Created by bruno on 04-11-2016.
 */

@Component  // For Spring AOP
@Aspect
public class PurchaseProfilerAspect {

    /**
     * get registry bean to log metric
     */
    @Autowired
    private MetricsService metricsService;


    @Around(value = "@annotation(ControllerActionMetrics)",
            argNames = "joinPoint,ControllerActionMetrics")
    public Object controllerActionMetrics(
            final ProceedingJoinPoint joinPoint,
            ControllerActionMetrics annotation) throws Throwable {
        final String signatureName = joinPoint.getSignature().getDeclaringTypeName() +
                "." + joinPoint.getSignature().getName();
        final Timer.Context successTimerContext = metricsService.getTimer(
                signatureName + ".timer.success").time();
        final Timer.Context errorTimerContext = metricsService.getTimer(
                signatureName + ".timer.errors").time();
        final Timer.Context requestTimerContext = metricsService.getTimer(
                signatureName + ".timer.requests").time();
        try {
            final Object retVal = joinPoint.proceed();
            successTimerContext.close();
            return retVal;
        }catch (Throwable e){
            errorTimerContext.close();
            throw e;
        }
        finally {
            requestTimerContext.close();
        }
    }
}