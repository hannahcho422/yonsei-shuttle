package com.yonsei.shuttle.location.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yonsei.shuttle.common.exception.CustomException;
import com.yonsei.shuttle.common.exception.ErrorCode;
import com.yonsei.shuttle.common.util.DistanceCalculator;
import com.yonsei.shuttle.location.domain.ShuttleLocation;
import com.yonsei.shuttle.location.dto.EtaResponse;
import com.yonsei.shuttle.location.dto.LocationResponse;
import com.yonsei.shuttle.location.dto.LocationUpdateRequest;
import com.yonsei.shuttle.location.repository.ShuttleLocationRepository;
import com.yonsei.shuttle.shuttle.domain.CityShuttle;
import com.yonsei.shuttle.shuttle.domain.RouteStop;
import com.yonsei.shuttle.shuttle.domain.Shuttle;
import com.yonsei.shuttle.shuttle.repository.CityShuttleRepository;
import com.yonsei.shuttle.shuttle.repository.RouteStopRepository;
import com.yonsei.shuttle.shuttle.repository.ShuttleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 셔틀 실시간 위치 비즈니스 로직
 * - 시뮬레이터로부터 위치 수신
 * - Redis 캐시 + PostgreSQL 이력 저장
 * - WebSocket push
 * - ETA 계산
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    private final ShuttleRepository shuttleRepository;
    private final ShuttleLocationRepository shuttleLocationRepository;
    private final CityShuttleRepository cityShuttleRepository;
    private final RouteStopRepository routeStopRepository;
    private final LocationCacheService locationCacheService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 위치 업데이트 수신 (시뮬레이터 → Backend)
     * 1. Redis 캐시 저장
     * 2. PostgreSQL 이력 저장
     * 3. WebSocket push
     */
    @Transactional
    public LocationResponse updateLocation(LocationUpdateRequest request) {
        Shuttle shuttle = shuttleRepository.findById(request.getShuttleId())
                .orElseThrow(() -> new CustomException(ErrorCode.SHUTTLE_NOT_FOUND));

        // 1. Redis 캐시 저장
        locationCacheService.cacheLocation(
                request.getShuttleId(),
                request.getLatitude(),
                request.getLongitude(),
                request.getHeading(),
                request.getSpeed()
        );

        // 2. PostgreSQL 이력 저장
        ShuttleLocation location = ShuttleLocation.builder()
                .shuttle(shuttle)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .heading(request.getHeading())
                .speed(request.getSpeed())
                .build();

        ShuttleLocation saved = shuttleLocationRepository.save(location);
        LocationResponse response = LocationResponse.from(saved);

        // 3. WebSocket push → /topic/shuttle-location/{shuttleId}
        messagingTemplate.convertAndSend(
                "/topic/shuttle-location/" + request.getShuttleId(),
                response
        );

        log.debug("위치 업데이트: shuttleId={}, lat={}, lon={}",
                request.getShuttleId(), request.getLatitude(), request.getLongitude());

        return response;
    }

    /**
     * 셔틀 현재 위치 조회
     * Redis 캐시 우선 → 없으면 DB fallback
     */
    @Transactional(readOnly = true)
    public LocationResponse getCurrentLocation(Integer shuttleId) {
        // Redis 캐시 먼저
        Optional<LocationResponse> cached = locationCacheService.getCachedLocation(shuttleId);
        if (cached.isPresent()) {
            return cached.get();
        }

        // DB fallback (최신 1건)
        ShuttleLocation location = shuttleLocationRepository
                .findTopByShuttle_ShuttleIdOrderByUpdatedAtDesc(shuttleId)
                .orElseThrow(() -> new CustomException(ErrorCode.LOCATION_NOT_FOUND));

        return LocationResponse.from(location);
    }

    /**
     * 모든 운행 중인 셔틀 위치 조회 (Redis 캐시 기반)
     */
    public List<LocationResponse> getAllCurrentLocations() {
        return locationCacheService.getAllCachedLocations();
    }

    /**
     * ETA 계산 — 현재 셔틀 위치 기준으로 노선의 각 정류장까지 예상 소요 시간
     *
     * @param shuttleId 셔틀 ID
     * @param routeId   노선 ID
     * @return 노선 정류장별 ETA 목록
     */
    @Transactional(readOnly = true)
    public List<EtaResponse> calculateEta(Integer shuttleId, Integer routeId) {
        // 현재 위치 조회
        LocationResponse currentLocation = getCurrentLocation(shuttleId);

        // 시내 셔틀 속도 조회 (기본 40km/h)
        double speedKmh = cityShuttleRepository.findByShuttle_ShuttleId(shuttleId)
                .map(CityShuttle::getSpeed)
                .map(BigDecimal::doubleValue)
                .orElse(40.0);

        // 노선별 정류장 목록 (순서대로)
        List<RouteStop> routeStops = routeStopRepository
                .findAllByRoute_RouteIdOrderBySequenceAsc(routeId);

        if (routeStops.isEmpty()) {
            return List.of();
        }

        // 각 정류장까지 거리 + ETA 계산
        double currentLat = currentLocation.getLatitude().doubleValue();
        double currentLon = currentLocation.getLongitude().doubleValue();

        List<EtaResponse> etaList = new ArrayList<>();
        double cumulativeDistanceKm = 0;

        // 현재 위치에서 첫 번째 정류장까지의 거리
        double prevLat = currentLat;
        double prevLon = currentLon;

        for (RouteStop routeStop : routeStops) {
            double stopLat = routeStop.getStop().getLatitude().doubleValue();
            double stopLon = routeStop.getStop().getLongitude().doubleValue();

            double segmentKm = DistanceCalculator.distanceKm(prevLat, prevLon, stopLat, stopLon);
            cumulativeDistanceKm += segmentKm;

            int etaMinutes = DistanceCalculator.estimateMinutes(cumulativeDistanceKm, speedKmh);

            etaList.add(EtaResponse.builder()
                    .stopId(routeStop.getStop().getStopId())
                    .stopName(routeStop.getStop().getStopName())
                    .sequence(routeStop.getSequence())
                    .distanceKm(BigDecimal.valueOf(cumulativeDistanceKm)
                            .setScale(2, java.math.RoundingMode.HALF_UP))
                    .estimatedMinutes(etaMinutes)
                    .build());

            prevLat = stopLat;
            prevLon = stopLon;
        }

        return etaList;
    }
}