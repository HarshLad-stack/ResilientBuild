package org.dev.velostack.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Resilient {

    // Retry configuration
    int maxRetries() default 3;

    long backoff() default 200; // initial delay in ms

    double backoffMultiplier() default 2.0;

    // Timeout configuration
    long timeout() default 2000; // milliseconds

    // Cache configuration
    boolean cacheEnabled() default true;

    long cacheTtl() default 60000; // 60 seconds

    // Fallback behavior
    boolean allowStale() default true;
}