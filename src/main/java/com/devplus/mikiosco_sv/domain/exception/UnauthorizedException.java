package com.devplus.mikiosco_sv.domain.exception;

public class UnauthorizedException extends BusinessException {

    public UnauthorizedException(String message) {
        super(message, 401);
    }
}
