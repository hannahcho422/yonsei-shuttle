"""
Backend API 클라이언트
"""
import requests
import logging
from typing import Optional

from config import API_BASE_URL

logger = logging.getLogger(__name__)


class ApiClient:
    """Backend API 호출 클라이언트"""

    def __init__(self, base_url: str = API_BASE_URL):
        self.base_url = base_url
        self.session = requests.Session()
        self.session.headers.update({"Content-Type": "application/json"})

    # ===== 조회 =====
    def get_all_shuttles(self, shuttle_type: Optional[str] = None) -> list:
        """
        전체 셔틀 목록 조회
        Note: /api/shuttles는 JWT 필요 → 별도의 Internal 엔드포인트가 없으므로
              현재는 시뮬레이터 내부적으로 공개 엔드포인트 가정. 추후 Internal API 추가 예정.
        """
        params = {"type": shuttle_type} if shuttle_type else {}
        try:
            res = self.session.get(f"{self.base_url}/api/shuttles", params=params, timeout=5)
            res.raise_for_status()
            return res.json()["data"]
        except requests.HTTPError as e:
            if e.response.status_code in (401, 403):
                logger.warning("셔틀 조회 인증 실패 - /api/internal/shuttles 엔드포인트가 필요합니다")
            raise

    def get_routes_by_shuttle(self, shuttle_id: int) -> list:
        res = self.session.get(f"{self.base_url}/api/shuttles/{shuttle_id}/routes", timeout=5)
        res.raise_for_status()
        return res.json()["data"]

    def get_stops_by_route(self, route_id: int) -> list:
        res = self.session.get(f"{self.base_url}/api/shuttles/routes/{route_id}/stops", timeout=5)
        res.raise_for_status()
        return res.json()["data"]

    def get_schedules_by_route(self, route_id: int) -> list:
        res = self.session.get(
            f"{self.base_url}/api/shuttles/routes/{route_id}/schedules", timeout=5
        )
        res.raise_for_status()
        return res.json()["data"]

    # ===== 위치 전송 (인증 없음) =====
    def update_location(self, shuttle_id: int, latitude: float, longitude: float,
                        heading: float = 0.0, speed: float = 0.0) -> dict:
        """
        /api/internal/location 엔드포인트로 위치 전송
        이 엔드포인트는 SecurityConfig에서 permitAll
        """
        payload = {
            "shuttleId": shuttle_id,
            "latitude": latitude,
            "longitude": longitude,
            "heading": heading,
            "speed": speed,
        }
        try:
            res = self.session.post(
                f"{self.base_url}/api/internal/location",
                json=payload,
                timeout=5,
            )
            res.raise_for_status()
            return res.json()["data"]
        except requests.RequestException as e:
            logger.error(f"위치 전송 실패 (shuttleId={shuttle_id}): {e}")
            raise