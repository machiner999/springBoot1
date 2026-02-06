package com.example.goldapi.exception;

public class GoldApiUnavailableException extends RuntimeException {
    public GoldApiUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
