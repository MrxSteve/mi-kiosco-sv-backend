package com.devplus.mikiosco_sv.infrastructure.persistence.entity.converter;

import com.devplus.mikiosco_sv.domain.model.DiscountType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class DiscountTypeConverter implements AttributeConverter<DiscountType, String> {

    @Override
    public String convertToDatabaseColumn(DiscountType attribute) {
        return attribute == null ? null : attribute.name().toLowerCase();
    }

    @Override
    public DiscountType convertToEntityAttribute(String dbData) {
        return dbData == null ? null : DiscountType.valueOf(dbData.toUpperCase());
    }
}
