package com.devplus.mikiosco_sv.infrastructure.persistence.entity.converter;

import com.devplus.mikiosco_sv.domain.model.AuditAction;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class AuditActionConverter implements AttributeConverter<AuditAction, String> {

    @Override
    public String convertToDatabaseColumn(AuditAction attribute) {
        return attribute == null ? null : attribute.name().toLowerCase();
    }

    @Override
    public AuditAction convertToEntityAttribute(String dbData) {
        return dbData == null ? null : AuditAction.valueOf(dbData.toUpperCase());
    }
}
