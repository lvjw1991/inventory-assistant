package com.example.recover.exception;

public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(int code, String message) {
        super(code, message);
    }
}
