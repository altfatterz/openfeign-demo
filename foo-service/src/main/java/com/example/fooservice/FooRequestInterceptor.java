package com.example.fooservice;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FooRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        log.debug("interceptor: {} {}", requestTemplate.request().httpMethod(), requestTemplate.request().url());
    }
}
