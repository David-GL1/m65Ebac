package com.ebac.modulo65.controller;

import org.springframework.http.ResponseEntity;

public class ResponseWrapper<T> {
    private final boolean success;
    private final String message;
    private final ResponseEntity<T> responseEntity;

    public ResponseWrapper(boolean success, String message, ResponseEntity<T> responseEntity) {
        this.success = success;
        this.message = message;
        this.responseEntity = responseEntity;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public ResponseEntity<T> getResponseEntity() {
        return responseEntity;
    }
}
