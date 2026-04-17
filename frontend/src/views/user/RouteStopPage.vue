<template>
  <section>
    <div class="container mt-5" style="padding-top: 30px; padding-bottom: 30px;">
      <div class="section-header text-left">
        <h1 class="mb-2">{{ routeName || '노선 정류장' }}</h1>
        <p class="section-subtitle">Route Stops</p>
      </div>

      <div v-if="loading" class="text-center py-5">
        <div class="spinner-border text-primary"></div>
      </div>

      <div v-else-if="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>

      <div v-else>
        <div v-if="stops.length === 0" class="text-center py-5 text-muted">
          정류장 정보가 없습니다.
        </div>

        <div v-else class="route-stops">
          <div v-for="(stop, idx) in stops" :key="stop.routeStopId"
            class="stop-item d-flex align-items-start mb-4">
            <!-- 순서 + 연결선 -->
            <div class="stop-indicator me-4">
              <div class="stop-number">{{ stop.sequence }}</div>
              <div v-if="idx !== stops.length - 1" class="stop-line"></div>
            </div>
            <!-- 정류장 정보 -->
            <div class="stop-info flex-grow-1 p-3 border rounded bg-white">
              <h4 class="mb-1" style="color: #3366CC;">{{ stop.stopName }}</h4>
              <p class="mb-1 text-muted small">
                <i class="fa fa-clock me-1"></i>
                출발지 기준 {{ stop.arrivalTimeMinutes }}분
              </p>
              <p class="mb-0 text-muted small">
                <i class="fa fa-map-marker-alt me-1"></i>
                {{ stop.latitude }}, {{ stop.longitude }}
              </p>
            </div>
          </div>
        </div>
      </div>

      <div class="mt-4">
        <router-link to="/routes" class="btn btn-outline-primary">
          <i class="fa fa-arrow-left me-2"></i>노선 목록으로
        </router-link>
        <router-link :to="`/routes/${routeId}/schedules`" class="btn btn-primary ms-2">
          시간표 보기 <i class="fa fa-arrow-right ms-2"></i>
        </router-link>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { shuttleApi } from '@/api/shuttle'

const route = useRoute()
const routeId = route.params.routeId

const stops = ref([])
const routeName = ref('')
const loading = ref(true)
const errorMessage = ref('')

onMounted(async () => {
  try {
    const [stopsRes, routeRes] = await Promise.all([
      shuttleApi.getStopsByRoute(routeId),
      shuttleApi.getRoute(routeId)
    ])
    stops.value = stopsRes.data.data
    routeName.value = routeRes.data.data.routeName
  } catch (error) {
    errorMessage.value = error.response?.data?.error?.message || '정류장 정보를 불러오는데 실패했습니다.'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.stop-indicator {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 40px;
}

.stop-number {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #1A76D1;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 1.1rem;
}

.stop-line {
  width: 2px;
  flex-grow: 1;
  background: #1A76D1;
  min-height: 40px;
  margin-top: 4px;
  margin-bottom: -16px;
}

.stop-info {
  transition: all 0.3s ease;
}

.stop-info:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transform: translateX(4px);
}
</style>