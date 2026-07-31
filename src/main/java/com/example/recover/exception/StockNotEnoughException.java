package com.example.recover.exception;

public class StockNotEnoughException extends BusinessException {
    public StockNotEnoughException(int code, String message) {
        super(code, message);
    }
}
