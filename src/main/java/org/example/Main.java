package org.example;

import org.example.utils.MonoFluxOperations;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {

        SpringApplication.run(MonoFluxOperations.class, args);
    }
}