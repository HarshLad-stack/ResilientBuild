package org.dev.velostack.Demo;


import org.dev.velostack.annotation.Resilient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FactService {
    private  final RestTemplate restTemplate= new RestTemplate();
    private  int attempt=0;

    @Resilient(maxRetries = 5,backoff = 2000,timeout = 3000)
    public  String getLiveFact(){
        attempt++;
        System.out.println("\n--- 🌐 Network Attempt #" + attempt + " ---");
        String url="https://dog-api.kinduff.com/api/facts";
        return  restTemplate.getForObject(url,String.class );
    }
    // Logic: Limit is 2 seconds, but the work takes 5 seconds!
    @Resilient(maxRetries = 3, timeout = 2000, backoff = 1000)
    public String simulateSlowNetwork() {
        System.out.println("\n--- ⏳ Timeout Test Attempt ---");

        try {
            // Pretend the API is taking 5 seconds to respond
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            System.out.println("🛑 Thread was successfully KILLED by the library!");
        }

        return "This should never return because timeout is 2s";
    }
    // Logic: Cache is ON for 30 seconds
    @Resilient(cacheEnabled = true, cacheTtl = 30000)
    public String getCachedFact() {
        System.out.println("🚀 [REAL METHOD] Talking to the Internet...");

        String url = "https://dog-api.kinduff.com/api/facts";
        return new RestTemplate().getForObject(url, String.class);
    }
}
