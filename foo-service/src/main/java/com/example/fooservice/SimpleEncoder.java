package com.example.fooservice;

import feign.RequestTemplate;
import feign.codec.Encoder;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;

@Slf4j
public class SimpleEncoder extends Encoder.Default {

    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) {
        log.info("SimpleEncoder called...");
        super.encode(object, bodyType, template);
    }
}
