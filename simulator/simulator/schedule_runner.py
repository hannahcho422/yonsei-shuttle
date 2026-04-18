"""
여러 노선 시뮬레이터를 관리하고 주기적으로 Backend에 위치 전송
"""
import logging
import time
from dataclasses import dataclass
from typing import Optional

from .api_client import ApiClient
from .position_generator import RouteSimulator
from .route_loader import RouteData

logger = logging.getLogger(__name__)


@dataclass
class ShuttleRunner:
    """각 셔틀별로 하나씩 실행되는 러너"""
    shuttle_id: int
    shuttle_name: str
    route: RouteData
    simulator: RouteSimulator
    start_time: float  # time.time() 기준 시작 시각


class ScheduleRunner:
    """
    전체 시뮬레이션 오케스트레이터
    - 각 셔틀-노선마다 RouteSimulator 인스턴스 생성
    - update_interval_seconds 마다 각 셔틀의 현재 위치 계산 + Backend 전송
    """

    def __init__(self, api: ApiClient, routes: list[RouteData],
                 update_interval_seconds: int = 3,
                 speed_multiplier: float = 10.0,
                 default_speed_kmh: float = 40.0):
        self.api = api
        self.update_interval = update_interval_seconds
        self.runners: list[ShuttleRunner] = []

        now = time.time()
        for route in routes:
            try:
                sim = RouteSimulator(route,
                                     speed_multiplier=speed_multiplier,
                                     default_speed_kmh=default_speed_kmh)
                # 셔틀당 첫 번째 노선만 사용 (간단화)
                # 한 셔틀이 여러 노선을 돌리려면 노선별로 다른 shuttle_id 필요
                self.runners.append(ShuttleRunner(
                    shuttle_id=route.shuttle_id,
                    shuttle_name=route.shuttle_name,
                    route=route,
                    simulator=sim,
                    start_time=now,
                ))
                logger.info(f"시뮬레이터 등록: {route.shuttle_name} - {route.route_name}")
            except ValueError as e:
                logger.warning(f"노선 {route.route_name} 스킵: {e}")

        # 같은 셔틀에 여러 노선이 있으면 첫 번째만 남기기 (shuttle_id 중복 제거)
        seen = set()
        unique_runners = []
        for r in self.runners:
            if r.shuttle_id not in seen:
                unique_runners.append(r)
                seen.add(r.shuttle_id)
            else:
                logger.info(
                    f"셔틀 {r.shuttle_name}({r.shuttle_id})에 이미 시뮬레이터 있음 "
                    f"→ {r.route.route_name} 스킵"
                )
        self.runners = unique_runners

    def tick(self):
        """한 번의 업데이트 주기 실행"""
        now = time.time()
        for runner in self.runners:
            elapsed = now - runner.start_time
            pos = runner.simulator.position_at(elapsed)

            try:
                self.api.update_location(
                    shuttle_id=runner.shuttle_id,
                    latitude=pos.latitude,
                    longitude=pos.longitude,
                    heading=pos.heading,
                    speed=pos.speed,
                )
                logger.info(
                    f"[{runner.shuttle_name}] "
                    f"({pos.latitude:.5f}, {pos.longitude:.5f}) "
                    f"{pos.current_stop_name} → {pos.next_stop_name} "
                    f"{pos.progress*100:.0f}%"
                )
            except Exception as e:
                logger.error(f"위치 전송 실패 [{runner.shuttle_name}]: {e}")

    def run_forever(self):
        """무한 루프로 실행"""
        logger.info(f"시뮬레이션 시작 - {len(self.runners)}대 셔틀 "
                    f"(주기: {self.update_interval}초)")
        try:
            while True:
                self.tick()
                time.sleep(self.update_interval)
        except KeyboardInterrupt:
            logger.info("시뮬레이션 종료 (Ctrl+C)")