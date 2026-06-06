package com.devplus.mikiosco_sv.domain.exception;

public class ForbiddenException extends BusinessException {

    public ForbiddenException(String message) {
        super(message, 403);
    }
}
