package com.example.fooservice;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.ConnectException;

import static org.springframework.http.HttpStatus.*;

@RestController
@Slf4j
public class FooController {

    private BarServiceClient barServiceClient;

    public FooController(BarServiceClient barServiceClient) {
        this.barServiceClient = barServiceClient;
    }

    @GetMapping("/foo")
    public String foo() {
        String barResponse = barServiceClient.bar();
        log.info("bar-service response: {}", barResponse);
        return "foo-service:" + barResponse;
    }

    @GetMapping("/foo/{id}")
    public String error(@PathVariable String id) {
        String response = barServiceClient.single(id);
        return "foo-service with error:" + response;
    }

    @ExceptionHandler(value = {ConnectException.class, FeignException.class})
    void handleDownstreamServiceException(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException {
        String message = "endpoint: " + request.getRequestURL() + " could not produce result because: " + e.getMessage();
        log.error(message);
        response.sendError(BAD_GATEWAY.value(), message);
    }

}
