package com.devplus.mikiosco_sv.domain.exception;

public class NotFoundException extends BusinessException {

    public NotFoundException(String message) {
        super(message, 404);
    }

    public static NotFoundException of(String entity, Object id) {
        return new NotFoundException(entity + " con id '" + id + "' no encontrado");
    }
}
