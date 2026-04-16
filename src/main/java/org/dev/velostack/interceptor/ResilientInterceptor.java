package org.dev.velostack.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.dev.velostack.annotation.Resilient;
import org.dev.velostack.cache.CacheStore;
import org.dev.velostack.retry.RetryHandler;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.function.Supplier;

@Aspect
@Component
public class    ResilientInterceptor {
    private final RetryHandler retryHandler= new RetryHandler();
    // RN TEH INTERCEPT METHOD RETURNS AN OBJECT BUT WE WILL RETURN A GENERICS SOMEDAY//
    //I PROMISE IT ILL ADD IN SOMETHIGN SO THAT THE SPRING AOP RETURNS GENRICS

    private static final CacheStore cacheStore= new CacheStore();

    @Around("@annotation(resilient)")
    public Object intercept(final ProceedingJoinPoint joinPoint, Resilient resilient) throws Throwable {

        // Unique ID for this specific method call (Method Name + Arguments)
        String cacheKey = generateKey(joinPoint);

        // STEP 1: The Fast Path (Cache Check)
        if (resilient.cacheEnabled()) {
            Object cached = cacheStore.get(cacheKey);
            if (cached != null) {
                System.out.println("🚀 [Cache HIT] Returning data instantly from RAM");
                return cached;
            }
        }

        // STEP 2: Wrapping the Work (Replacing the Lambda)
        // We create a 'Supplier' object that holds the instructions to run the method
        Supplier<Object> workToExecute = new Supplier<Object>() {
            @Override
            public Object get() {
                return proceed(joinPoint); // This runs your real method (e.g., getLiveFact)
            }
        };

        // STEP 3: The Hard Path (Retry + Timeout Engine)
        Object result = retryHandler.execute(
                workToExecute,
                resilient.maxRetries(),
                resilient.backoff(),
                resilient.backoffMultiplier(),
                resilient.timeout()
        );
        if (resilient.cacheEnabled() && result != null) {
            cacheStore.put(cacheKey, result, resilient.cacheTtl());
            System.out.println("💾 [Cache PUT] Saving result to RAM for key: " + cacheKey);
        }

        return result;


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
    private String generateKey(ProceedingJoinPoint joinPoint){
        String methodName=joinPoint.getSignature().toShortString();
        String args= Arrays.toString(joinPoint.getArgs());
        return  methodName+":"+args;

    }

}
