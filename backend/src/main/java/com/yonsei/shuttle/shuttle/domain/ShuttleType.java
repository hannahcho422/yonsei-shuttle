package com.yonsei.shuttle.shuttle.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 셔틀 종류
 * DB 저장값: '시내' | '시외' (PostgreSQL ENUM)
 * 
 * PostgreSQL에서 CREATE TYPE shuttle_type AS ENUM ('시내', '시외'); 로 생성
 * Java Enum은 한글 이름을 쓸 수 없음
 * Java는 영문, DB는 한글로 매핑하는 AttributeConverter를 사용
 */
@Getter
@RequiredArgsConstructor
public enum ShuttleType {
    CITY("시내"),
    INTERCITY("시외");

    private final String dbValue;

    public static ShuttleType fromDbValue(String dbValue) {
        for (ShuttleType type : values()) {
            if (type.dbValue.equals(dbValue)) return type;
        }
        throw new IllegalArgumentException("Unknown shuttle_type: " + dbValue);
    }
}