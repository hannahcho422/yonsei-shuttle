package com.yonsei.shuttle.shuttle.dto;

import com.yonsei.shuttle.shuttle.domain.Shuttle;
import com.yonsei.shuttle.shuttle.domain.ShuttleType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShuttleResponse {

    private final Integer shuttleId;
    private final String name;
    private final ShuttleType type;
    private final Integer capacity;

    public static ShuttleResponse from(Shuttle shuttle) {
        return ShuttleResponse.builder()
                .shuttleId(shuttle.getShuttleId())
                .name(shuttle.getName())
                .type(shuttle.getType())
                .capacity(shuttle.getCapacity())
                .build();
    }
}