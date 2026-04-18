"""
두 정류장 사이의 좌표를 선형 보간하여 연속 위치 생성
"""
import math
from dataclasses import dataclass
from typing import Iterator

from .route_loader import RouteData, StopData


@dataclass
class Position:
    """생성된 셔틀 위치"""
    latitude: float
    longitude: float
    heading: float  # 진행 방향 (0~360도, 정북=0, 시계방향)
    speed: float    # km/h
    progress: float  # 현재 구간 진행률 0.0 ~ 1.0
    current_stop_name: str
    next_stop_name: str


def haversine_km(lat1: float, lon1: float, lat2: float, lon2: float) -> float:
    """두 좌표 사이 거리 (km)"""
    R = 6371.0
    d_lat = math.radians(lat2 - lat1)
    d_lon = math.radians(lon2 - lon1)
    a = (math.sin(d_lat / 2) ** 2
         + math.cos(math.radians(lat1)) * math.cos(math.radians(lat2))
         * math.sin(d_lon / 2) ** 2)
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    return R * c


def bearing_degrees(lat1: float, lon1: float, lat2: float, lon2: float) -> float:
    """두 좌표 사이 진행 방향 (0~360°, 정북=0)"""
    lat1_r = math.radians(lat1)
    lat2_r = math.radians(lat2)
    d_lon = math.radians(lon2 - lon1)

    x = math.sin(d_lon) * math.cos(lat2_r)
    y = (math.cos(lat1_r) * math.sin(lat2_r)
         - math.sin(lat1_r) * math.cos(lat2_r) * math.cos(d_lon))
    bearing = math.degrees(math.atan2(x, y))
    return (bearing + 360) % 360


def interpolate(start: StopData, end: StopData, progress: float) -> tuple[float, float]:
    """두 정류장 사이 선형 보간 (progress: 0.0 ~ 1.0)"""
    lat = start.latitude + (end.latitude - start.latitude) * progress
    lon = start.longitude + (end.longitude - start.longitude) * progress
    return lat, lon


class RouteSimulator:
    """
    노선 따라 셔틀 위치를 시간 흐름대로 생성하는 시뮬레이터
    - 각 구간의 예상 소요 시간(arrival_time_minutes 차이)을 기반으로 progress 계산
    - speed_multiplier: 시뮬레이션 시간 가속 (10.0 = 10배 빠름)
    - default_speed_kmh: 속도 보고용 (실제 이동 속도는 시간 기반)
    """

    def __init__(self, route: RouteData, speed_multiplier: float = 1.0,
                 default_speed_kmh: float = 40.0):
        if len(route.stops) < 2:
            raise ValueError(f"노선 {route.route_name}: 정류장이 2개 미만이라 시뮬레이션 불가")

        self.route = route
        self.speed_multiplier = speed_multiplier
        self.default_speed_kmh = default_speed_kmh

        # 총 운행 시간 (분) = 마지막 정류장의 arrival_time_minutes
        self.total_duration_minutes = route.stops[-1].arrival_time_minutes
        if self.total_duration_minutes <= 0:
            # 시간 정보가 없으면 거리 기반 계산
            total_km = self._total_distance_km()
            self.total_duration_minutes = (total_km / default_speed_kmh) * 60

    def _total_distance_km(self) -> float:
        total = 0.0
        for i in range(len(self.route.stops) - 1):
            s1, s2 = self.route.stops[i], self.route.stops[i + 1]
            total += haversine_km(s1.latitude, s1.longitude, s2.latitude, s2.longitude)
        return total

    def position_at(self, elapsed_seconds: float) -> Position:
        """
        운행 시작으로부터 elapsed_seconds 경과한 시점의 셔틀 위치를 계산
        speed_multiplier가 적용된 가상 시간 사용
        """
        virtual_minutes = (elapsed_seconds * self.speed_multiplier) / 60.0

        # 루프 운행 (노선 끝까지 가면 다시 처음부터)
        cycle_minute = virtual_minutes % self.total_duration_minutes

        # 현재 어느 구간에 있는지 찾기
        for i in range(len(self.route.stops) - 1):
            curr = self.route.stops[i]
            nxt = self.route.stops[i + 1]
            segment_duration = nxt.arrival_time_minutes - curr.arrival_time_minutes

            if segment_duration <= 0:
                continue

            if curr.arrival_time_minutes <= cycle_minute < nxt.arrival_time_minutes:
                # 이 구간에 있음
                progress = (cycle_minute - curr.arrival_time_minutes) / segment_duration
                lat, lon = interpolate(curr, nxt, progress)
                heading = bearing_degrees(curr.latitude, curr.longitude,
                                          nxt.latitude, nxt.longitude)
                return Position(
                    latitude=lat,
                    longitude=lon,
                    heading=heading,
                    speed=self.default_speed_kmh,
                    progress=progress,
                    current_stop_name=curr.stop_name,
                    next_stop_name=nxt.stop_name,
                )

        # 마지막 정류장에 도착한 상태
        last = self.route.stops[-1]
        return Position(
            latitude=last.latitude,
            longitude=last.longitude,
            heading=0.0,
            speed=0.0,
            progress=1.0,
            current_stop_name=last.stop_name,
            next_stop_name=last.stop_name,
        )