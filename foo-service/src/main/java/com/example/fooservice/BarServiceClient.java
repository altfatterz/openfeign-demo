package com.example.fooservice;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("bar-service")
interface BarServiceClient {

    @GetMapping("/success")
    Bar success();

    @GetMapping("/failure-with-status-200")
    Bar failWithStatus200();

    @GetMapping("/failure-with-status-non-200")
    Bar failWithStatusNon200();

    @GetMapping("/bar/{id}")
    String decode404(@PathVariable String id);
}

@Data
class Bar {
    String value;
}

