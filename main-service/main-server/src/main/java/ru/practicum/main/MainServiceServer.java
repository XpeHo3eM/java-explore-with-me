package ru.practicum.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"ru.practicum.main", "ru.practicum.stats.client"})
public class MainServiceServer {
    public static void main(String[] args) {
        SpringApplication.run(MainServiceServer.class, args);
    }
}