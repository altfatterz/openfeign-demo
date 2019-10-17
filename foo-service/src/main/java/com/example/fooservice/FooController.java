package com.example.fooservice;

import feign.FeignException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.ConnectException;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;

@RestController
@Slf4j
public class FooController {

    private BarServiceClient barServiceClient;

    public FooController(BarServiceClient barServiceClient) {
        this.barServiceClient = barServiceClient;
    }

    @GetMapping("/success")
    Foo success() {
        Bar response = barServiceClient.success();
        log.info("bar-service response: {}", response);
        Foo foo = new Foo(UUID.randomUUID().toString(), response.getValue());
        return foo;
    }

    @GetMapping("/failure-with-status-200")
    Foo handleFailureWithStatus200() {
        Bar response = barServiceClient.failWithStatus200();
        log.info("bar-service response: {}", response);
        Foo foo = new Foo(UUID.randomUUID().toString(), response.getValue());
        return foo;
    }

    @GetMapping("/failure-with-status-non-200")
    Foo handleFailureWithStatusNon200() {
        Bar response = barServiceClient.failWithStatusNon200();
        log.info("bar-service response: {}", response);
        Foo foo = new Foo(UUID.randomUUID().toString(), response.getValue());
        return foo;
    }

    @GetMapping("/decode404/{id}")
    public Foo decode404(@PathVariable String id) {
        String response = barServiceClient.decode404(id);
        log.info("bar-service response: {}", response);
        Foo foo = new Foo(UUID.randomUUID().toString(), "default value");
        return foo;
    }

    @ExceptionHandler(value = {ConnectException.class, FeignException.class, IllegalArgumentException.class})
    void handleDownstreamServiceException(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException {
        String message = "endpoint: " + request.getRequestURL() + " could not produce result because: " + e.getMessage();
        log.error(message);
        response.sendError(BAD_GATEWAY.value(), message);
    }

    @Data
    static class Foo {
        String id;
        String barValue;

        public Foo(String id, String barValue) {
            Assert.hasLength(barValue, "barValue cannot be empty");
            this.id = id;
            this.barValue = barValue;
        }
    }

}

