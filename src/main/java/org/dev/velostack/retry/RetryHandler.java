package org.dev.velostack.retry;

import java.util.concurrent.*;
import java.util.function.Supplier;

public class RetryHandler {

    private  final ExecutorService executorService= Executors.newCachedThreadPool();
    public <T> T execute(Supplier<T> action,
                         int maxRetries,
                         long initialBackoff,
                         double backofMultiplier,
                         long timeoutMillis
                         ){

        if(maxRetries<0){
            throw new IllegalArgumentException("maxRetries cannot be lesser than zero");
        }
        int attempt=0;
        long currentBackoff=initialBackoff;
        Throwable lastException=null;
        while(attempt<=maxRetries){
            try{
                return  executeWithTimeout(action,timeoutMillis);
            }catch (Throwable ex){
                lastException=ex;
                if (attempt==maxRetries){
                    break;
                }
                System.out.println("⚠️ Attempt " + (attempt + 1) + " failed. Retrying in " + currentBackoff + "ms...");
               waitForNextAttempt(currentBackoff);
                currentBackoff=(long)(currentBackoff*backofMultiplier);
                attempt++;
            }
        }
        throw new RuntimeException("Resilient execution failed after " + (attempt) + " retries", lastException);
    }
    private  void sleep(long millis){
        try{
            Thread.sleep(millis);
        }catch (InterruptedException e){
            throw new RuntimeException("Retry interuppeted",e);
        }
    }

    private <T> T executeWithTimeout(Supplier<T> action, long timeoutMillis) throws Exception {
        // OPTIONAL: If user set timeout to 0, run it normally (no threads)
        if (timeoutMillis <= 0) {
            return action.get();
        }

        // ASYNC: Run in a separate thread lane
        CompletableFuture<T> future = CompletableFuture.supplyAsync(action, executorService);

        try {
            return future.get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true); // Stop the worker!
            throw new RuntimeException("Action timed out after " + timeoutMillis + "ms");
        } catch (ExecutionException e) {
            // Unwrap the real error from the background thread
            throw (Exception) e.getCause();
        }
    }
    private RuntimeException wrapIfChecked(Throwable throwable){
        if (throwable instanceof  RuntimeException){
            return (RuntimeException) throwable;
        }
        return new RuntimeException(throwable);
    }
    private void waitForNextAttempt(long millis){
        try{
            Thread.sleep(millis);
        }
        catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }
}
