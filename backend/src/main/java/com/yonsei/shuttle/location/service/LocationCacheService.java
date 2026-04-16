package com.yonsei.shuttle.location.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.yonsei.shuttle.location.dto.LocationResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Redis 기반 셔틀 위치 캐시
 *
 * Key 구조: shuttle:location:{shuttleId}
 * Value: Hash { latitude, longitude, heading, speed, updatedAt }
 * TTL: 5분 (위치 업데이트가 없으면 자동 만료)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LocationCacheService {

    private static final String KEY_PREFIX = "shuttle:location:";
    private static final long TTL_MINUTES = 5;

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 위치 캐시 저장 (덮어쓰기)
     */
    public void cacheLocation(Integer shuttleId, BigDecimal latitude, BigDecimal longitude,
                               BigDecimal heading, BigDecimal speed) {
        String key = KEY_PREFIX + shuttleId;

        Map<String, String> locationMap = new HashMap<>();
        locationMap.put("shuttleId", String.valueOf(shuttleId));
        locationMap.put("latitude", latitude.toPlainString());
        locationMap.put("longitude", longitude.toPlainString());
        locationMap.put("heading", heading != null ? heading.toPlainString() : "");
        locationMap.put("speed", speed != null ? speed.toPlainString() : "");
        locationMap.put("updatedAt", OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        redisTemplate.opsForHash().putAll(key, locationMap);
        redisTemplate.expire(key, TTL_MINUTES, TimeUnit.MINUTES);

        log.debug("위치 캐시 저장: shuttleId={}, lat={}, lon={}", shuttleId, latitude, longitude);
    }

    /**
     * 캐시에서 셔틀 최신 위치 조회
     */
    public Optional<LocationResponse> getCachedLocation(Integer shuttleId) {
        String key = KEY_PREFIX + shuttleId;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);

        if (entries.isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.of(LocationResponse.of(
                    Integer.valueOf((String) entries.get("shuttleId")),
                    new BigDecimal((String) entries.get("latitude")),
                    new BigDecimal((String) entries.get("longitude")),
                    parseOptionalBigDecimal((String) entries.get("heading")),
                    parseOptionalBigDecimal((String) entries.get("speed")),
                    OffsetDateTime.parse((String) entries.get("updatedAt"))
            ));
        } catch (Exception e) {
            log.warn("위치 캐시 파싱 실패: shuttleId={}, error={}", shuttleId, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * 모든 셔틀의 캐시된 위치 조회
     */
    public List<LocationResponse> getAllCachedLocations() {
        Set<String> keys = redisTemplate.keys(KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return List.of();
        }

        List<LocationResponse> locations = new ArrayList<>();
        for (String key : keys) {
            String shuttleIdStr = key.replace(KEY_PREFIX, "");
            getCachedLocation(Integer.valueOf(shuttleIdStr))
                    .ifPresent(locations::add);
        }

        return locations;
    }

    private BigDecimal parseOptionalBigDecimal(String value) {
        return (value == null || value.isEmpty()) ? null : new BigDecimal(value);
    }
}