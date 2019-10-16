package com.example.fooservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("bar-service")
interface BarServiceClient {

    @GetMapping("/bar")
    String bar();

    @GetMapping("/bar/{id}")
    String single(@PathVariable String id);
}
