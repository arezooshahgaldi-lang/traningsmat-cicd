package com.example.traningsmat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // Slår på Spring Boots auto-konfiguration + komponentskanning av paketet
public class TraningsmatApplication {

    public static void main(String[] args) {
        SpringApplication.run(TraningsmatApplication.class, args);
    }
}

