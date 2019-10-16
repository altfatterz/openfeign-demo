package com.example.barservice;

import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@SpringBootApplication
@RestController
public class BarServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BarServiceApplication.class, args);
    }

    @GetMapping("/ok")
    public String ok() {
        return "Hello, I am the bar-service";
    }

    @GetMapping("/fail")
    public void fail() {
        throw new RuntimeException("boom");
    }

    @GetMapping("/bar-failed")
    public ResponseEntity<Map<String, Object>> failWithStatus200() {
        Map<String, Object> body = new LinkedHashMap();
        body.put("timestamp", new Date());
        body.put("message", "something went wrong");
        body.put("status", 500);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/bar-success")
    public Bar bar() {
        Bar bar = new Bar();
        bar.setValue("hello");
        return bar;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> notFound(@PathVariable String id) {
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @Data
    static class Bar {
        String value;
    }

}
