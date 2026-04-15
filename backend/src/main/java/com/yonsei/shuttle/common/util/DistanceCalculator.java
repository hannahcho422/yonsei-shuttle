package com.yonsei.shuttle.common.util;

/**
 * 두 GPS 좌표 간 거리/ETA 계산 유틸
 * Haversine 공식 사용 (지구 곡률 반영)
 */
public final class DistanceCalculator {

    private static final double EARTH_RADIUS_KM = 6371.0;

    private DistanceCalculator() {
        // 유틸 클래스는 인스턴스화 금지
    }

    /**
     * 두 좌표 간 거리 (km)
     *
     * @param lat1 좌표1 위도
     * @param lon1 좌표1 경도
     * @param lat2 좌표2 위도
     * @param lon2 좌표2 경도
     * @return 거리 (km)
     */
    public static double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    /**
     * 거리 + 속도 → 소요 시간 (분)
     *
     * @param distanceKm 거리 (km)
     * @param speedKmh   평균 속도 (km/h)
     * @return 소요 시간 (분), 속도가 0이하면 -1
     */
    public static int estimateMinutes(double distanceKm, double speedKmh) {
        if (speedKmh <= 0) return -1;
        return (int) Math.round((distanceKm / speedKmh) * 60);
    }
}