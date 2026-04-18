"""
연세 셔틀 시뮬레이터 진입점
"""
import logging
import sys

import config
from simulator.api_client import ApiClient
from simulator.route_loader import load_all_city_shuttle_routes
from simulator.schedule_runner import ScheduleRunner


def setup_logging():
    logging.basicConfig(
        level=getattr(logging, config.LOG_LEVEL),
        format='%(asctime)s [%(levelname)s] %(name)s - %(message)s',
        datefmt='%H:%M:%S',
    )


def main():
    setup_logging()
    logger = logging.getLogger(__name__)

    logger.info("=" * 60)
    logger.info("연세 셔틀 시뮬레이터 시작")
    logger.info(f"  API_BASE_URL: {config.API_BASE_URL}")
    logger.info(f"  UPDATE_INTERVAL: {config.UPDATE_INTERVAL_SECONDS}초")
    logger.info(f"  SPEED_MULTIPLIER: {config.SIMULATION_SPEED_MULTIPLIER}x")
    logger.info("=" * 60)

    api = ApiClient(base_url=config.API_BASE_URL)

    try:
        routes = load_all_city_shuttle_routes(api)
    except Exception as e:
        logger.error(f"노선 로드 실패: {e}")
        logger.error("Backend가 실행 중인지 확인해주세요 (http://localhost:8080)")
        sys.exit(1)

    if not routes:
        logger.warning("시뮬레이션할 노선이 없습니다.")
        logger.warning("시내 셔틀에 정류장이 연결된 노선이 필요합니다.")
        sys.exit(0)

    runner = ScheduleRunner(
        api=api,
        routes=routes,
        update_interval_seconds=config.UPDATE_INTERVAL_SECONDS,
        speed_multiplier=config.SIMULATION_SPEED_MULTIPLIER,
        default_speed_kmh=config.DEFAULT_SPEED_KMH,
    )

    runner.run_forever()


if __name__ == "__main__":
    main()