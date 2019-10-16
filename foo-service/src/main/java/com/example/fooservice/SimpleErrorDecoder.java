package com.example.fooservice;

import com.example.fooservice.FooServiceApplication.DownstreamServiceException;
import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import static feign.FeignException.errorStatus;

@Slf4j
public class SimpleErrorDecoder implements ErrorDecoder {

    @Override
    public DownstreamServiceException decode(String methodKey, Response response) {
        FeignException exception = errorStatus(methodKey, response);
        DownstreamServiceException result = new DownstreamServiceException(exception.getMessage());
        log.error("downstream-service error: {}", result.getMessage());
        return result;
    }
}
