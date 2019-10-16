package com.example.barservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/bar/{id}")
    public String errorWith200(@PathVariable  String id) {
        throw new RuntimeException("something went wrong");
    }
}
