package com.example.fooservice;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class FooRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        System.out.printf("I was here");
    }
}
