package com.yonsei.shuttle.reservation.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ReservationStatusConverter implements AttributeConverter<ReservationStatus, String> {

    @Override
    public String convertToDatabaseColumn(ReservationStatus attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public ReservationStatus convertToEntityAttribute(String dbValue) {
        return dbValue == null ? null : ReservationStatus.fromDbValue(dbValue);
    }
}