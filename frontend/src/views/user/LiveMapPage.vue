<template>
  <section>
    <div class="container-fluid mt-5" style="padding-top: 30px; padding-bottom: 30px;">
      <div class="section-header text-left container">
        <h1 class="mb-2">실시간 셔틀 위치</h1>
        <p class="section-subtitle">Live Shuttle Location</p>
      </div>

      <div class="container">
        <!-- 셔틀 선택 -->
        <div class="row mb-3">
          <div class="col-md-4">
            <label class="form-label"><strong>셔틀 선택</strong></label>
            <select class="form-select" v-model="selectedShuttleId" @change="onShuttleChange">
              <option value="">전체 셔틀</option>
              <option v-for="shuttle in shuttles" :key="shuttle.shuttleId"
                :value="shuttle.shuttleId">
                {{ shuttle.name }} ({{ shuttle.type === 'CITY' ? '시내' : '시외' }})
              </option>
            </select>
          </div>
          <div class="col-md-4 d-flex align-items-end">
            <div>
              <span class="badge" :class="connectionStatus === 'connected' ? 'bg-success' : 'bg-secondary'">
                {{ connectionStatus === 'connected' ? '● 실시간 연결됨' : '○ 연결 대기중' }}
              </span>
              <span class="ms-2 text-muted small">
                운행 중: {{ Object.keys(shuttleMarkers).length }}대
              </span>
            </div>
          </div>
        </div>

        <!-- 지도 -->
        <div id="map" class="map-container"></div>

        <!-- 범례 -->
        <div class="mt-3 small text-muted">
          <i class="fa fa-info-circle me-1"></i>
          위치가 표시되지 않는 셔틀은 현재 운행하지 않거나 시뮬레이터가 데이터를 보내고 있지 않습니다.
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'
import SockJS from 'sockjs-client/dist/sockjs'
import { Client } from '@stomp/stompjs'
import { shuttleApi } from '@/api/shuttle'
import { locationApi } from '@/api/location'

// 연세대 미래캠퍼스 중심 좌표
const CAMPUS_CENTER = [37.3428, 127.9219]

const shuttles = ref([])
const selectedShuttleId = ref('')
const connectionStatus = ref('disconnected')

let map = null
let shuttleMarkers = ref({})  // { shuttleId: marker }
let stompClient = null

// ===== Leaflet 지도 초기화 =====
function initMap() {
  map = L.map('map').setView(CAMPUS_CENTER, 14)

  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap contributors',
    maxZoom: 19
  }).addTo(map)
}

// ===== 커스텀 버스 아이콘 =====
function createBusIcon() {
  return L.divIcon({
    className: 'bus-marker',
    html: `<div class="bus-marker-inner"><i class="fa fa-bus"></i></div>`,
    iconSize: [40, 40],
    iconAnchor: [20, 20]
  })
}

// ===== 마커 생성/업데이트 =====
function updateMarker(shuttleId, location) {
  const { latitude, longitude, speed, heading } = location
  const lat = parseFloat(latitude)
  const lng = parseFloat(longitude)

  const popupContent = `
    <div>
      <strong>${getShuttleName(shuttleId)}</strong><br>
      속도: ${speed || 0} km/h<br>
      방향: ${heading || 0}°<br>
      <small>${new Date(location.updatedAt).toLocaleTimeString('ko-KR')}</small>
    </div>
  `

  if (shuttleMarkers.value[shuttleId]) {
    shuttleMarkers.value[shuttleId].setLatLng([lat, lng])
    shuttleMarkers.value[shuttleId].getPopup().setContent(popupContent)
  } else {
    const marker = L.marker([lat, lng], { icon: createBusIcon() })
      .addTo(map)
      .bindPopup(popupContent)
    shuttleMarkers.value[shuttleId] = marker
  }
}

function getShuttleName(shuttleId) {
  const s = shuttles.value.find((x) => x.shuttleId === shuttleId)
  return s ? s.name : `셔틀 #${shuttleId}`
}

// ===== 초기 위치 로드 (Redis 캐시) =====
async function loadInitialLocations() {
  try {
    const res = await locationApi.getAllLocations()
    res.data.data.forEach((loc) => updateMarker(loc.shuttleId, loc))
  } catch (error) {
    console.warn('초기 위치 조회 실패:', error.message)
  }
}

// ===== WebSocket 연결 =====
function connectWebSocket() {
  stompClient = new Client({
    webSocketFactory: () => new SockJS('/ws'),
    reconnectDelay: 5000,
    debug: () => {},
    onConnect: () => {
      connectionStatus.value = 'connected'
      console.log('WebSocket connected')
      subscribeToShuttles()
    },
    onDisconnect: () => {
      connectionStatus.value = 'disconnected'
      console.log('WebSocket disconnected')
    },
    onStompError: (frame) => {
      console.error('STOMP error:', frame)
    }
  })
  stompClient.activate()
}

function subscribeToShuttles() {
  shuttles.value.forEach((shuttle) => {
    stompClient.subscribe(`/topic/shuttle-location/${shuttle.shuttleId}`, (message) => {
      try {
        const location = JSON.parse(message.body)
        updateMarker(shuttle.shuttleId, location)
      } catch (e) {
        console.error('메시지 파싱 실패:', e)
      }
    })
  })
}

// ===== 셔틀 선택 시 지도 이동 =====
function onShuttleChange() {
  if (!selectedShuttleId.value) return
  const marker = shuttleMarkers.value[parseInt(selectedShuttleId.value)]
  if (marker) {
    map.setView(marker.getLatLng(), 16)
    marker.openPopup()
  }
}

// ===== 생명주기 =====
onMounted(async () => {
  initMap()

  try {
    const res = await shuttleApi.getAll()
    shuttles.value = res.data.data
  } catch (error) {
    console.error('셔틀 목록 조회 실패:', error)
  }

  await loadInitialLocations()
  connectWebSocket()
})

onBeforeUnmount(() => {
  if (stompClient) stompClient.deactivate()
  if (map) map.remove()
})
</script>

<style>
.map-container {
  width: 100%;
  height: 600px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.bus-marker {
  background: transparent !important;
  border: none !important;
}

.bus-marker-inner {
  width: 40px;
  height: 40px;
  background: #1A76D1;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { box-shadow: 0 0 0 0 rgba(26, 118, 209, 0.7); }
  70% { box-shadow: 0 0 0 10px rgba(26, 118, 209, 0); }
  100% { box-shadow: 0 0 0 0 rgba(26, 118, 209, 0); }
}
</style>