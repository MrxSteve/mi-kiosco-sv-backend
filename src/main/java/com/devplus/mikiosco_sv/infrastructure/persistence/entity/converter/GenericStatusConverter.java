package com.devplus.mikiosco_sv.infrastructure.persistence.entity.converter;

import com.devplus.mikiosco_sv.domain.model.GenericStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class GenericStatusConverter implements AttributeConverter<GenericStatus, String> {

    @Override
    public String convertToDatabaseColumn(GenericStatus status) {
        return status == null ? null : status.name().toLowerCase();
    }

    @Override
    public GenericStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : GenericStatus.valueOf(dbData.toUpperCase());
    }
}
