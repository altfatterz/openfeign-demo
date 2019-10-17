package com.example.fooservice;

import feign.FeignException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
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
    String success() {
        String response = barServiceClient.ok();
        log.info("bar-service response: {}", response);
        return "foo-service:" + response;
    }

    @GetMapping("/failure")
    String failure() {
        String response = barServiceClient.fail();
        log.info("bar-service response: {}", response);
        return "foo-service:" + response;
    }

    @GetMapping("/foo-success")
    Foo fooSuccess() {
        Bar response = barServiceClient.barSuccess();
        log.info("bar-service response: {}", response);
        Foo foo = new Foo();
        foo.setId(UUID.randomUUID().toString());
        foo.setBarValue(response.getValue());
        return foo;
    }

    @GetMapping("/foo-failure")
    Foo fooFailure() {
        Bar response = barServiceClient.barFailed();
        log.info("bar-service response: {}", response);
        Foo foo = new Foo();
        foo.setId(UUID.randomUUID().toString());
        foo.setBarValue(response.getValue());
        return foo;
    }

    @GetMapping("/decode404/{id}")
    public Foo one(@PathVariable String id) {
        String response = barServiceClient.one(id);
        log.info("bar-service response: {}", response);
        Foo foo = new Foo();
        foo.setId(UUID.randomUUID().toString());
        foo.setBarValue(response);
        return foo;
    }

    @ExceptionHandler(value = {ConnectException.class, FeignException.class})
    void handleDownstreamServiceException(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException {
        String message = "endpoint: " + request.getRequestURL() + " could not produce result because: " + e.getMessage();
        log.error(message);
        response.sendError(BAD_GATEWAY.value(), message);
    }

    @Data
    static class Foo {
        String id;
        String barValue;
    }

}

