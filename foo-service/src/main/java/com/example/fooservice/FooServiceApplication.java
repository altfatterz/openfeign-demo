package com.example.fooservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@SpringBootApplication
@RestController
@EnableFeignClients
@Slf4j
public class FooServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FooServiceApplication.class, args);
    }

    @Autowired
    private BarServiceClient barServiceClient;

    @GetMapping("/foo")
    public String foo() {
        String barResponse = barServiceClient.bar();
        log.info("bar-service response: {}", barResponse);
        return "foo-service:" + barResponse;
    }

    @GetMapping("/error")
    public String error() {
        return "foo-service with error:" + barServiceClient.error();
    }

    @ExceptionHandler(DownstreamServiceException.class)
    void handle(HttpServletResponse response, DownstreamServiceException e) throws IOException {
        response.sendError(SERVICE_UNAVAILABLE.value(), e.getMessage());
    }

    @FeignClient("bar")
    interface BarServiceClient {

        @GetMapping("/bar")
        String bar();

        @GetMapping("/error")
        String error();
    }

    public static class DownstreamServiceException extends RuntimeException {
        public DownstreamServiceException(String message) {
            super(message);
        }
    }

}
