package com.novaSup.InventoryGest.InventoryGest_Backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MainBackend {

    public static void main(String[] args) {
        SpringApplication.run(MainBackend.class, args);
        System.out.println("Backend Spring Boot iniciado.");
    }
}
