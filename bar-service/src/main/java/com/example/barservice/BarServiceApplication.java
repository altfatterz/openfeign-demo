package com.example.barservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class BarServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BarServiceApplication.class, args);
    }

    @GetMapping("/bar")
    public String bar() {
        return "Hello, I am the bar-service";
    }

    @GetMapping("/error")
    public String errorWith200() {
        throw new IllegalStateException("something went wrong");
    }
}
