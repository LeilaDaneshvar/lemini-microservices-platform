package com.lemini.users;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = {"com.lemini.users"})
public class UserApplication {

    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(UserApplication.class, args);
    }
    
}
