FROM python:3.12-slim

WORKDIR /app

# requirements 먼저 복사해서 의존성 캐시 활용
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 소스 복사
COPY config.py .
COPY main.py .
COPY simulator simulator

# 환경 변수로 API_BASE_URL 오버라이드 가능하게
ENV PYTHONUNBUFFERED=1

CMD ["python", "main.py"]