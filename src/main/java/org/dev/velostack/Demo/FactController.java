package org.dev.velostack.Demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FactController {
    private  FactService factService;
    public  FactController(FactService factService){
        this.factService=factService;

    }
    @GetMapping("/get-fact")
    public  String fetch(){
        return  "Server says"+factService.getLiveFact();
    }
    @GetMapping("/sloenet")
    public  String getch(){
        return "Server says"+factService.simulateSlowNetwork();
    }
    @GetMapping("/test-cache")
    public String testCache() {
        return factService.getCachedFact();
    }
}
