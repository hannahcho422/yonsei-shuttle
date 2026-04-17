<template>
  <section>
    <div class="container mt-5" style="padding-top: 30px; padding-bottom: 30px;">
      <div class="section-header text-left">
        <h1 class="mb-2">{{ routeName || '노선 시간표' }}</h1>
        <p class="section-subtitle">Schedule</p>
      </div>

      <!-- 요일 필터 -->
      <ul class="nav nav-pills mb-4">
        <li class="nav-item">
          <button class="nav-link" :class="{ active: !dayFilter }"
            @click="dayFilter = ''">전체</button>
        </li>
        <li class="nav-item">
          <button class="nav-link" :class="{ active: dayFilter === 'MON-FRI' }"
            @click="dayFilter = 'MON-FRI'">평일 (월-금)</button>
        </li>
        <li class="nav-item">
          <button class="nav-link" :class="{ active: dayFilter === 'SAT' }"
            @click="dayFilter = 'SAT'">토요일</button>
        </li>
        <li class="nav-item">
          <button class="nav-link" :class="{ active: dayFilter === 'SUN' }"
            @click="dayFilter = 'SUN'">일요일</button>
        </li>
      </ul>

      <div v-if="loading" class="text-center py-5">
        <div class="spinner-border text-primary"></div>
      </div>

      <div v-else-if="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>

      <div v-else>
        <div v-if="filteredSchedules.length === 0" class="text-center py-5 text-muted">
          운행 시간 정보가 없습니다.
        </div>

        <div v-else class="row">
          <div v-for="schedule in filteredSchedules" :key="schedule.scheduleId"
            class="col-md-3 col-sm-4 col-6 mb-3">
            <div class="schedule-time-card text-center p-3 border rounded bg-white shadow-sm">
              <div class="schedule-day small text-muted mb-1">{{ schedule.dayOfWeek }}</div>
              <div class="schedule-time" style="font-size: 1.5rem; font-weight: bold; color: #1A76D1;">
                {{ schedule.departureTime.slice(0, 5) }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="mt-4">
        <router-link to="/routes" class="btn btn-outline-primary">
          <i class="fa fa-arrow-left me-2"></i>노선 목록으로
        </router-link>
        <router-link :to="`/routes/${routeId}/stops`" class="btn btn-primary ms-2">
          정류장 보기 <i class="fa fa-arrow-right ms-2"></i>
        </router-link>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { shuttleApi } from '@/api/shuttle'

const route = useRoute()
const routeId = route.params.routeId

const schedules = ref([])
const routeName = ref('')
const loading = ref(true)
const errorMessage = ref('')
const dayFilter = ref('')

const filteredSchedules = computed(() => {
  if (!dayFilter.value) return schedules.value
  return schedules.value.filter((s) => s.dayOfWeek === dayFilter.value)
})

onMounted(async () => {
  try {
    const [schedulesRes, routeRes] = await Promise.all([
      shuttleApi.getSchedulesByRoute(routeId),
      shuttleApi.getRoute(routeId)
    ])
    schedules.value = schedulesRes.data.data
    routeName.value = routeRes.data.data.routeName
  } catch (error) {
    errorMessage.value = error.response?.data?.error?.message || '시간표를 불러오는데 실패했습니다.'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.schedule-time-card {
  transition: all 0.3s ease;
  cursor: default;
}

.schedule-time-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.1);
}
</style>