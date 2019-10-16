package com.example.fooservice;

import feign.Response;
import feign.codec.Decoder.Default;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Type;

@Slf4j
public class SimpleDecoder extends Default {

    // whey is Type for bar-success a String and for /bar-failed com.example.fooservice.Bar ???
    @Override
    public Object decode(Response response, Type type) throws IOException {
        log.info("SimpleDecoder called...");
        return super.decode(response, type);
    }
}
