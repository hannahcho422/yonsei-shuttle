# 연세 셔틀 - 시스템 아키텍처

```mermaid
flowchart TB
    subgraph Internet["🌐 Internet"]
        User["👤 사용자<br/>(웹 브라우저)"]
    end

    subgraph Cloudflare["☁️ Cloudflare Edge"]
        CF["Cloudflare<br/>(HTTPS + DDoS 방어)"]
        CFTunnel["🔒 Cloudflare Tunnel<br/>(암호화 터널)"]
    end

    subgraph RaspberryPi["🥧 Raspberry Pi 4 (Ubuntu Server)"]
        direction TB
        Cloudflared["cloudflared<br/>(Tunnel Daemon)"]

        subgraph Docker["🐳 Docker Compose Network"]
            direction TB
            Nginx["Nginx<br/>Reverse Proxy<br/>:80"]

            subgraph Apps["Application Layer"]
                direction LR
                Frontend["Vue.js 3<br/>+ Nginx<br/>Frontend"]
                Backend["Spring Boot 4<br/>+ WebSocket<br/>Backend :8080"]
                Simulator["Python 3.12<br/>Simulator"]
            end

            subgraph Data["Data Layer"]
                direction LR
                Postgres[("PostgreSQL 16<br/>영속 데이터")]
                Redis[("Redis 7<br/>위치 캐시")]
            end
        end
    end

    User -->|HTTPS| CF
    CF --> CFTunnel
    CFTunnel -.암호화 터널.-> Cloudflared
    Cloudflared --> Nginx

    Nginx -->|/| Frontend
    Nginx -->|/api/*| Backend
    Nginx -->|/ws/*| Backend

    Backend -->|JPA| Postgres
    Backend -->|RedisTemplate| Redis
    Simulator -->|POST /api/internal/location| Backend
    Backend -->|STOMP push| Nginx
    Backend -.캐시 저장.-> Redis
    Redis -.위치 조회.-> Backend

    classDef external fill:#e3f2fd,stroke:#1976d2,stroke-width:2px,color:#000
    classDef cloud fill:#fff4e1,stroke:#f57c00,stroke-width:2px,color:#000
    classDef pi fill:#fce4ec,stroke:#c2185b,stroke-width:2px,color:#000
    classDef docker fill:#e8f5e9,stroke:#388e3c,stroke-width:2px,color:#000
    classDef app fill:#f3e5f5,stroke:#7b1fa2,stroke-width:2px,color:#000
    classDef data fill:#fff9c4,stroke:#f9a825,stroke-width:2px,color:#000

    class User external
    class CF,CFTunnel cloud
    class Cloudflared pi
    class Nginx,Frontend,Backend,Simulator app
    class Postgres,Redis data
```

## 주요 흐름

### 1. 일반 요청 (REST API)
```
사용자 → Cloudflare HTTPS → Cloudflare Tunnel → cloudflared → Nginx
  → /api/* → Backend → PostgreSQL / Redis
```

### 2. 실시간 위치 (WebSocket)
```
사용자 → WebSocket 연결 (/ws) → Backend (STOMP broker)
Simulator → POST /api/internal/location → Backend
  → Redis 캐싱 + DB 저장
  → STOMP push → 구독 중인 모든 클라이언트
```

### 3. 정적 파일 (Frontend SPA)
```
사용자 → Cloudflare → Tunnel → Nginx
  → / → Frontend(Nginx in container) → index.html + JS/CSS
```

## 기술 스택

| Layer | Tech |
|---|---|
| Edge | Cloudflare Tunnel (무료), Cloudflare HTTPS |
| Host OS | Ubuntu Server 22.04 LTS (aarch64) |
| Hardware | Raspberry Pi 4 |
| Container | Docker + Docker Compose |
| Reverse Proxy | Nginx |
| Backend | Java 21, Spring Boot 4, Spring Security + JWT, JPA, WebSocket(STOMP) |
| Frontend | Vue 3 (Composition API), Vite, Pinia, Vue Router, Bootstrap 5, Leaflet |
| Simulator | Python 3.12, requests |
| Database | PostgreSQL 16 |
| Cache | Redis 7 |
| Tools | GitHub, Gradle, npm |

## 포트 구성

| 서비스 | 컨테이너 포트 | 외부 노출 |
|---|---|---|
| Nginx | 80 | ❌ (Cloudflare Tunnel만) |
| Frontend | 80 | ❌ (내부 네트워크) |
| Backend | 8080 | ❌ (내부 네트워크) |
| PostgreSQL | 5432 | ❌ (내부 네트워크) |
| Redis | 6379 | ❌ (내부 네트워크) |
| Simulator | - | ❌ (outbound only) |

> **보안**: 외부에 직접 노출되는 포트 없음. Cloudflare Tunnel이 유일한 진입점.
