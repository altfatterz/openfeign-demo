package com.example.fooservice;

import feign.Response;
import feign.codec.Decoder.Default;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Type;

@Slf4j
public class SimpleDecoder extends Default {

    @Override
    public Object decode(Response response, Type type) throws IOException {
        log.info("SimpleDecoder with type: {}", type.getTypeName());
        return super.decode(response, type);
    }
}
