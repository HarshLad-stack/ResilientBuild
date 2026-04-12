package org.dev.velostack;

import org.dev.velostack.annotation.Resilient;
import org.springframework.stereotype.Service;


public class DemoService {
    private  int count=0;
    @Resilient(maxRetries = 3,backoff = 100)
    public  String unableCall(){
        count++;
        System.out.println("Call attempt: " + count);
        if(count<3){
            throw new RuntimeException("Fail");
        }
        return "Success";
    }
}
