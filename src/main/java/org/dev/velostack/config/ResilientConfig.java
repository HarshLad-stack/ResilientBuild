package org.dev.velostack.config;


import org.dev.velostack.DemoService;
import org.dev.velostack.interceptor.ResilientInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class ResilientConfig {

    @Bean
    public ResilientInterceptor resilientInterceptor(){
        return  new ResilientInterceptor();
    }
    @Bean
    public DemoService demoService(){
        return new DemoService();
    }
}
