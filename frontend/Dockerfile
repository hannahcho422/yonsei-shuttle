# ===== Build Stage =====
FROM node:20-alpine AS build

WORKDIR /app

# package.json 먼저 복사해서 의존성 캐시 활용
COPY package*.json ./
RUN npm install

# 소스 복사 후 빌드
COPY . .
RUN npm run build

# ===== Runtime Stage: Nginx =====
FROM nginx:alpine

# 기본 nginx 설정 제거 후 커스텀 설정 복사
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 빌드된 정적 파일 복사
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]