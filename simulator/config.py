"""
시뮬레이터 전역 설정
"""

# Backend API 주소
API_BASE_URL = "http://localhost:8080"

# 위치 업데이트 주기 (초)
UPDATE_INTERVAL_SECONDS = 3

# 시뮬레이션 속도 (실제 시간 대비 배수)
# 1.0 = 실제 속도, 10.0 = 10배 빠름 (테스트용)
SIMULATION_SPEED_MULTIPLIER = 10.0

# 기본 평균 속도 (km/h) - city_shuttle.speed 기본값
DEFAULT_SPEED_KMH = 40.0

# 운행할 셔틀 ID 목록 (시내 셔틀만)
# None 이면 Backend에서 모든 시내 셔틀을 자동 조회
TARGET_SHUTTLE_IDS = None

# 로그 레벨: DEBUG, INFO, WARNING, ERROR
LOG_LEVEL = "INFO"