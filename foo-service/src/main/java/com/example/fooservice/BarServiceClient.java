package com.example.fooservice;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

    @GetMapping("/bar/{id}")
    String one(@PathVariable String id);
}

@Data
class Bar {
    String value;
}