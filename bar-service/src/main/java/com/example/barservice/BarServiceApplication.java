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

    @GetMapping("/success")
    public Bar bar() {
        Bar bar = new Bar();
        bar.setValue("hello");
        return bar;
    }

    @GetMapping("/failure-with-status-200")
    public Error failWithStatus200() {
        Error error = new Error();
        error.setMessage("something went wrong");
        error.setStatus(500);
        return error;
    }

    @GetMapping("/failure-with-status-non-200")
    public ResponseEntity<Error> failWithStatusNon200() {
        Error error = new Error();
        error.setMessage("something went wrong again");
        error.setStatus(500);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/bar/{id}")
    public ResponseEntity<?> notFound(@PathVariable String id) {
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @Data
    static class Bar {
        String value;
    }

    @Data
    static class Error {
        String message;
        int status;
    }

}
