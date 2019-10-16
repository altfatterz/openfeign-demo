package com.example.fooservice;

import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;

import static feign.FeignException.errorStatus;

public class SimpleErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        FeignException exception = errorStatus(methodKey, response);
        return exception;
    }
}
