package com.lemini.discovery;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServiceApplication {

    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(DiscoveryServiceApplication.class, args);
    }    

}
