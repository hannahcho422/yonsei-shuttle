<template>
  <section>
    <div class="container mt-5" style="padding-top: 30px; padding-bottom: 30px;">
      <div class="section-header text-left">
        <h1 class="mb-2">예약 내역</h1>
        <p class="section-subtitle">Reservation History</p>
      </div>

      <div v-if="loading" class="text-center py-5">
        <div class="spinner-border text-primary"></div>
      </div>

      <div v-else-if="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>

      <div v-else>
        <div v-if="reservations.length === 0" class="text-center py-5 text-muted">
          예약 내역이 없습니다.
        </div>

        <table v-else class="table table-bordered">
          <thead class="table-light">
            <tr>
              <th class="text-center">예약번호</th>
              <th>노선</th>
              <th class="text-center">탑승일</th>
              <th class="text-center">출발시간</th>
              <th class="text-center">좌석</th>
              <th class="text-center">상태</th>
              <th class="text-center">취소</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="r in reservations" :key="r.reservationId">
              <td class="text-center">{{ r.reservationId }}</td>
              <td>{{ r.routeName }}</td>
              <td class="text-center">{{ r.travelDate }}</td>
              <td class="text-center">{{ r.departureTime.slice(0, 5) }}</td>
              <td class="text-center">{{ r.seatNum }}번</td>
              <td class="text-center">
                <span class="badge" :class="statusBadgeClass(r.status)">
                  {{ r.status === 'RESERVED' ? '예약' : '취소' }}
                </span>
              </td>
              <td class="text-center">
                <button v-if="r.status === 'RESERVED'"
                  class="btn btn-sm btn-outline-danger"
                  @click="cancelReservation(r.reservationId)">
                  취소
                </button>
                <span v-else class="text-muted small">
                  {{ formatDate(r.cancelledAt) }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { reservationApi } from '@/api/reservation'

const reservations = ref([])
const loading = ref(true)
const errorMessage = ref('')

async function loadReservations() {
  loading.value = true
  errorMessage.value = ''
  try {
    const res = await reservationApi.getMyList()
    reservations.value = res.data.data
  } catch (error) {
    errorMessage.value = error.response?.data?.error?.message || '예약 내역을 불러오는데 실패했습니다.'
  } finally {
    loading.value = false
  }
}

async function cancelReservation(id) {
  if (!confirm('예약을 취소하시겠습니까?')) return
  try {
    await reservationApi.cancel(id)
    alert('예약이 취소되었습니다.')
    loadReservations()
  } catch (error) {
    alert(error.response?.data?.error?.message || '취소에 실패하였습니다.')
  }
}

function statusBadgeClass(status) {
  return status === 'RESERVED' ? 'bg-primary' : 'bg-secondary'
}

function formatDate(dateString) {
  if (!dateString) return ''
  return new Date(dateString).toLocaleString('ko-KR', {
    month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit'
  })
}

onMounted(loadReservations)
</script>