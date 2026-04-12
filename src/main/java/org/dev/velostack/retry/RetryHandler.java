package org.dev.velostack.retry;

import java.util.function.Supplier;

public class RetryHandler {
    public <T> T execute(Supplier<T> action,
                         int maxRetries,
                         long initialBackoff,
                         double backofMultiplier
                         ){
        int attempt=0;
        long currentBackoff=initialBackoff;
        Throwable lastException=null;
        while(attempt<=maxRetries){
            try{
                return  action.get();
            }catch (Throwable ex){
                lastException=ex;
                if (attempt==maxRetries){
                    break;
                }
                sleep(currentBackoff);
                currentBackoff=(long)(currentBackoff*backofMultiplier);
                attempt++;
            }
        }
        throw wrapIfChecked(lastException);
    }
    private  void sleep(long millis){
        try{
            Thread.sleep(millis);
        }catch (InterruptedException e){
            throw new RuntimeException("Retry interuppeted",e);
        }
    }
    private RuntimeException wrapIfChecked(Throwable throwable){
        if (throwable instanceof  RuntimeException){
            return (RuntimeException) throwable;
        }
        return new RuntimeException(throwable);
    }
}
