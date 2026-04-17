"""
Backend에서 시뮬레이션할 노선/정류장 데이터 로드
"""
import logging
from dataclasses import dataclass
from typing import Optional

from .api_client import ApiClient

logger = logging.getLogger(__name__)


@dataclass
class StopData:
    stop_id: int
    stop_name: str
    latitude: float
    longitude: float
    sequence: int
    arrival_time_minutes: int


@dataclass
class RouteData:
    route_id: int
    route_name: str
    shuttle_id: int
    shuttle_name: str
    stops: list[StopData]


def load_routes_for_shuttle(api: ApiClient, shuttle_id: int) -> list[RouteData]:
    """
    셔틀의 모든 노선과 각 노선의 정류장을 로드
    정류장이 없는 노선은 제외
    """
    routes_json = api.get_routes_by_shuttle(shuttle_id)
    loaded = []

    for route in routes_json:
        try:
            stops_json = api.get_stops_by_route(route["routeId"])
            if not stops_json:
                logger.info(f"노선 {route['routeId']} ({route['routeName']}): 정류장 없음 → skip")
                continue

            stops = [
                StopData(
                    stop_id=s["stopId"],
                    stop_name=s["stopName"],
                    latitude=float(s["latitude"]),
                    longitude=float(s["longitude"]),
                    sequence=s["sequence"],
                    arrival_time_minutes=s["arrivalTimeMinutes"],
                )
                for s in stops_json
            ]
            stops.sort(key=lambda s: s.sequence)

            loaded.append(RouteData(
                route_id=route["routeId"],
                route_name=route["routeName"],
                shuttle_id=route["shuttleId"],
                shuttle_name=route["shuttleName"],
                stops=stops,
            ))
            logger.info(
                f"노선 로드: {route['shuttleName']} / {route['routeName']} "
                f"(정류장 {len(stops)}개)"
            )
        except Exception as e:
            logger.error(f"노선 {route['routeId']} 로드 실패: {e}")

    return loaded


def load_all_city_shuttle_routes(api: ApiClient) -> list[RouteData]:
    """
    시내 셔틀의 모든 노선 로드 (시뮬레이션 대상)
    """
    shuttles = api.get_all_shuttles(shuttle_type="CITY")
    logger.info(f"시내 셔틀 {len(shuttles)}대 발견")

    all_routes = []
    for shuttle in shuttles:
        routes = load_routes_for_shuttle(api, shuttle["shuttleId"])
        all_routes.extend(routes)

    return all_routes