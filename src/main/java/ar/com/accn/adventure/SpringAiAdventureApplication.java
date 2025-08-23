package com.example.springaiadventure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Spring AI Adventure application.  This class
 * simply boots the Spring context and exposes the REST controller
 * defined elsewhere in the project.
 */
@SpringBootApplication
public class SpringAiAdventureApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiAdventureApplication.class, args);
    }
}