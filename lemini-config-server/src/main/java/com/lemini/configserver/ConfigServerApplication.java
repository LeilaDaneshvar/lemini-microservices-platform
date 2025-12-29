package com.lemini.configserver;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(ConfigServerApplication.class, args);
    }

}
