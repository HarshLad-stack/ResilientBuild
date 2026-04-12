package org.dev.velostack.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.dev.velostack.annotation.Resilient;
import org.dev.velostack.retry.RetryHandler;

import java.util.function.Supplier;

public class    ResilientInterceptor {
    private final RetryHandler retryHandler= new RetryHandler();
    @Around("@annotation(resilient)")// RN TEH INTERCEPT METHOD RETURNS AN OBJECT BUT WE WILL RETURN A GENERICS SOMEDAY//
    //I PROMISE IT ILL ADD IN SOMETHIGN SO THAT THE SPRING AOP RETURNS GENRICS
    public Object intercept(final ProceedingJoinPoint joinPoint,    Resilient resilient) throws Throwable {

        // We hand the logic over to our engine
        return retryHandler.execute(new Supplier<Object>() {
                                        @Override
                                        public Object get() {
                                            // This is the "Start Button" for the original method
                                            return proceed(joinPoint);
                                        }
                                    },
                resilient.maxRetries(),
                resilient.backoff(),
                resilient.backoffMultiplier());
    }
    private Object proceed(ProceedingJoinPoint joinPoint) {
        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            throw wrap(throwable);
        }
    }

    private RuntimeException wrap(Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            return (RuntimeException) throwable;
        }
        return new RuntimeException(throwable);
    }
}
