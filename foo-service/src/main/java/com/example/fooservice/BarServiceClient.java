package com.example.fooservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient("bar-service")
interface BarServiceClient {

    @GetMapping("/ok")
    String ok();

    @GetMapping("/fail")
    String fail();

    @GetMapping("/bar-failed")
    Bar barFailed();

    @GetMapping("/bar-success")
    Bar barSuccess();

    @GetMapping("/{id}")
    String one(@PathVariable String id);
}

class Bar {
    String value;
}