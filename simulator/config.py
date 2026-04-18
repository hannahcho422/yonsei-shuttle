"""
시뮬레이터 전역 설정
환경변수로 오버라이드 가능 (Docker 실행 시 활용)
"""
import os

# Backend API 주소
API_BASE_URL = os.getenv("API_BASE_URL", "http://localhost:8080")

# 위치 업데이트 주기 (초)
UPDATE_INTERVAL_SECONDS = int(os.getenv("UPDATE_INTERVAL_SECONDS", "3"))

# 시뮬레이션 속도 (실제 시간 대비 배수)
SIMULATION_SPEED_MULTIPLIER = float(os.getenv("SIMULATION_SPEED_MULTIPLIER", "10.0"))

# 기본 평균 속도 (km/h)
DEFAULT_SPEED_KMH = float(os.getenv("DEFAULT_SPEED_KMH", "40.0"))

# 운행할 셔틀 ID 목록
TARGET_SHUTTLE_IDS = None

# 로그 레벨
LOG_LEVEL = os.getenv("LOG_LEVEL", "INFO")