package com.devplus.mikiosco_sv.domain.exception;

public class BusinessException extends RuntimeException {

    private final int statusCode;

    public BusinessException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
