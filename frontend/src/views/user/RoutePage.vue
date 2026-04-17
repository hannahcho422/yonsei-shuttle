<template>
  <section>
    <div class="container mt-5" style="padding-top: 30px; padding-bottom: 30px;">
      <div class="section-header text-left">
        <h1 class="mb-2">{{ pageTitle }}</h1>
        <p class="section-subtitle">{{ pageSubtitle }}</p>
      </div>

      <!-- 타입 탭 -->
      <ul class="nav nav-pills mb-4">
        <li class="nav-item">
          <router-link class="nav-link" :class="{ active: !typeFilter }" to="/routes">
            전체
          </router-link>
        </li>
        <li class="nav-item">
          <router-link class="nav-link" :class="{ active: typeFilter === 'CITY' }"
            to="/routes?type=CITY">
            시내 셔틀
          </router-link>
        </li>
        <li class="nav-item">
          <router-link class="nav-link" :class="{ active: typeFilter === 'INTERCITY' }"
            to="/routes?type=INTERCITY">
            시외 셔틀
          </router-link>
        </li>
      </ul>

      <div v-if="loading" class="text-center py-5">
        <div class="spinner-border text-primary"></div>
      </div>

      <div v-else-if="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>

      <div v-else>
        <div v-if="filteredRoutes.length === 0" class="text-center py-5 text-muted">
          노선 정보가 없습니다.
        </div>

        <div v-else class="row">
          <div v-for="route in filteredRoutes" :key="route.routeId"
            class="col-md-6 col-lg-4 mb-4">
            <div class="card-hover" style="width: 100%; height: 220px; cursor: pointer;"
              @click="goToStops(route.routeId)">
              <h4>{{ route.shuttleName }}</h4>
              <p class="text-muted mb-2">{{ route.direction }}</p>
              <p class="text-center flex-grow-1">{{ route.routeName }}</p>
              <div class="mt-auto d-flex gap-2 w-100 justify-content-center">
                <button class="btn btn-sm btn-outline-primary"
                  @click.stop="goToStops(route.routeId)">
                  정류장
                </button>
                <button class="btn btn-sm btn-outline-primary"
                  @click.stop="goToSchedules(route.routeId)">
                  시간표
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { shuttleApi } from '@/api/shuttle'

const route = useRoute()
const router = useRouter()
const routes = ref([])
const loading = ref(true)
const errorMessage = ref('')

const typeFilter = computed(() => route.query.type || '')

const pageTitle = computed(() => {
  if (typeFilter.value === 'CITY') return '시내 셔틀 노선'
  if (typeFilter.value === 'INTERCITY') return '시외 셔틀 노선'
  return '전체 노선'
})

const pageSubtitle = computed(() => 'Shuttle Routes')

const filteredRoutes = computed(() => {
  if (!typeFilter.value) return routes.value
  // shuttleName에서 타입 추론은 불가하므로 Backend에서 받은 전체 노선을 사용
  // 추후 Backend에 타입 필터 API 추가하거나, shuttle 정보로 필터링
  return routes.value
})

async function loadRoutes() {
  loading.value = true
  errorMessage.value = ''
  try {
    let response
    if (typeFilter.value) {
      // 특정 타입의 셔틀들을 먼저 가져온 후 각 셔틀의 노선을 수집
      const shuttleRes = await shuttleApi.getAll(typeFilter.value)
      const shuttles = shuttleRes.data.data
      const routeArrays = await Promise.all(
        shuttles.map((s) => shuttleApi.getRoutesByShuttle(s.shuttleId))
      )
      routes.value = routeArrays.flatMap((r) => r.data.data)
    } else {
      response = await shuttleApi.getAllRoutes()
      routes.value = response.data.data
    }
  } catch (error) {
    errorMessage.value = error.response?.data?.error?.message || '노선 정보를 불러오는데 실패했습니다.'
  } finally {
    loading.value = false
  }
}

onMounted(loadRoutes)
watch(typeFilter, loadRoutes)

function goToStops(routeId) {
  router.push(`/routes/${routeId}/stops`)
}

function goToSchedules(routeId) {
  router.push(`/routes/${routeId}/schedules`)
}
</script>