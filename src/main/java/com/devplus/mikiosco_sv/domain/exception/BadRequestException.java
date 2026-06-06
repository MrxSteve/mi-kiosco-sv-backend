package com.devplus.mikiosco_sv.domain.exception;

public class BadRequestException extends BusinessException {

    public BadRequestException(String message) {
        super(message, 400);
    }
}
