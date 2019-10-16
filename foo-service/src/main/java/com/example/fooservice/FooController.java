package com.example.fooservice;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.ConnectException;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestController
@Slf4j
public class FooController {

    private BarServiceClient barServiceClient;

    public FooController(BarServiceClient barServiceClient) {
        this.barServiceClient = barServiceClient;
    }

    @GetMapping("/ok")
    String foo() {
        String response = barServiceClient.ok();
        log.info("bar-service response: {}", response);
        return "foo-service:" + response;
    }

    @GetMapping("/fail")
    String fail() {
        String response = barServiceClient.fail();
        log.info("bar-service response: {}", response);
        return "foo-service:" + response;
    }

    @GetMapping("/bar-sucecss")
    String barSuccess() {
        Bar response = barServiceClient.barSuccess();
        log.info("bar-service response: {}", response);
        return "foo-service:" + response;
    }

    @GetMapping("/bar-failed")
    String barFailed() {
        Bar response = barServiceClient.barFailed();
        log.info("bar-service response: {}", response);
        return "foo-service:" + response;
    }

    @GetMapping("/{id}")
    public String one(@PathVariable String id) {
        String response = barServiceClient.one(id);
        return "foo-service:" + (response == null ? "" : response);
    }

    @ExceptionHandler(value = {ConnectException.class, FeignException.class})
    void handleDownstreamServiceException(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException {
        String message = "endpoint: " + request.getRequestURL() + " could not produce result because: " + e.getMessage();
        log.error(message);
        response.sendError(BAD_GATEWAY.value(), message);
    }

}
