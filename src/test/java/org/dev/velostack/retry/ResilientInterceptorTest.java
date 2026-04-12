package org.dev.velostack.retry;

import org.aspectj.lang.ProceedingJoinPoint;
import org.dev.velostack.interceptor.ResilientInterceptor;
import org.dev.velostack.annotation.Resilient; // This is the key import
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ResilientInterceptorTest {
    static class TestService {
        AtomicInteger counter = new AtomicInteger();

        @Resilient(maxRetries = 2, backoff = 1)
        public String unstable() {
            if (counter.getAndIncrement() < 2) {
                throw new RuntimeException("fail");
            }
            return "ok";
        }
    }

    @Test
    void shouldRetryViaInterceptor() throws Throwable {
        TestService service = new TestService();
        Method method = TestService.class.getMethod("unstable");
        Resilient annotation = method.getAnnotation(Resilient.class);

        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        when(pjp.proceed()).thenAnswer(invocation -> service.unstable());

        ResilientInterceptor interceptor = new ResilientInterceptor();
        Object result = interceptor.intercept(pjp, annotation);

        assertEquals("ok", result);
        assertEquals(3, service.counter.get());
    }
}