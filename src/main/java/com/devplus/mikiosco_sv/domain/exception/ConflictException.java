package com.devplus.mikiosco_sv.domain.exception;

public class ConflictException extends BusinessException {

    public ConflictException(String message) {
        super(message, 409);
    }
}
