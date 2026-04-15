package com.yonsei.shuttle.shuttle.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


/**
 * 셔틀 종류
 * DB 저장값: '시내' | '시외' (PostgreSQL ENUM)
 * 
 * PostgreSQL에서 CREATE TYPE shuttle_type AS ENUM ('시내', '시외'); 로 생성
 * Java Enum은 한글 이름을 쓸 수 없음
 * Java는 영문, DB는 한글로 매핑하는 AttributeConverter를 사용
 */
@Converter(autoApply = true)
public class ShuttleTypeConverter implements AttributeConverter<ShuttleType, String> {

    @Override
    public String convertToDatabaseColumn(ShuttleType attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public ShuttleType convertToEntityAttribute(String dbValue) {
        return dbValue == null ? null : ShuttleType.fromDbValue(dbValue);
    }
}