package com.devplus.mikiosco_sv.infrastructure.persistence.entity.converter;

import com.devplus.mikiosco_sv.domain.model.SubscriptionStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class SubscriptionStatusConverter implements AttributeConverter<SubscriptionStatus, String> {

    @Override
    public String convertToDatabaseColumn(SubscriptionStatus status) {
        return status == null ? null : status.name().toLowerCase();
    }

    @Override
    public SubscriptionStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : SubscriptionStatus.valueOf(dbData.toUpperCase());
    }
}
