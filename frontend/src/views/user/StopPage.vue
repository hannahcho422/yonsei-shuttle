<template>
  <section>
    <div class="container mt-5" style="padding-top: 30px; padding-bottom: 30px;">
      <div class="section-header text-left">
        <h1 class="mb-2">승하차 장소</h1>
        <p class="section-subtitle">Bus Stops</p>
      </div>

      <div v-if="loading" class="text-center py-5">
        <div class="spinner-border text-primary"></div>
      </div>

      <div v-else-if="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>

      <div v-else>
        <div v-if="stops.length === 0" class="text-center py-5 text-muted">
          정류장 정보가 없습니다.
        </div>

        <div v-else class="row">
          <div v-for="stop in stops" :key="stop.stopId"
            class="col-md-6 col-lg-4 mb-4">
            <div class="card-hover" style="width: 100%; min-height: 200px;">
              <h4>{{ stop.stopName }}</h4>
              <p class="mb-1 text-muted small text-center">
                <i class="fa fa-map-marker-alt me-1"></i>
                위도: {{ stop.latitude }}
              </p>
              <p class="mb-0 text-muted small text-center">
                <i class="fa fa-map-marker-alt me-1"></i>
                경도: {{ stop.longitude }}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { shuttleApi } from '@/api/shuttle'

const stops = ref([])
const loading = ref(true)
const errorMessage = ref('')

onMounted(async () => {
  try {
    const response = await shuttleApi.getAllStops()
    stops.value = response.data.data
  } catch (error) {
    errorMessage.value = error.response?.data?.error?.message || '정류장 정보를 불러오는데 실패했습니다.'
  } finally {
    loading.value = false
  }
})
</script> 