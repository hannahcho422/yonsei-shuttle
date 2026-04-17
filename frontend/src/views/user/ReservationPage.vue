<template>
  <section>
    <div class="container mt-5" style="padding-top: 30px; padding-bottom: 30px;">
      <div class="section-header text-left">
        <h1 class="mb-2">시외 셔틀 예약</h1>
        <p class="section-subtitle">Intercity Shuttle Reservation</p>
      </div>

      <!-- 날짜 선택 -->
      <div class="row mb-4">
        <div class="col-md-4">
          <label for="date" class="form-label h5"><strong>날짜 선택</strong></label>
          <input type="date" id="date" class="form-control"
            v-model="selectedDate" :min="today" @change="loadShuttleList" />
        </div>
      </div>

      <!-- 셔틀 목록 -->
      <div v-if="loading" class="text-center py-5">
        <div class="spinner-border text-primary"></div>
      </div>

      <div v-else-if="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>

      <div v-else-if="!selectedDate" class="text-muted py-4">
        날짜를 선택해주세요.
      </div>

      <div v-else-if="scheduleList.length === 0" class="text-muted py-4">
        선택한 날짜에 이용 가능한 셔틀이 없습니다.
      </div>

      <div v-else>
        <h5 class="mb-3"><strong>셔틀 목록</strong></h5>
        <table class="table table-bordered">
          <thead class="table-light">
            <tr>
              <th class="text-center">종류</th>
              <th>노선</th>
              <th class="text-center">출발 시간</th>
              <th class="text-center">잔여 좌석</th>
              <th class="text-center">예약</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in scheduleList" :key="item.scheduleId">
              <td class="text-center">{{ item.direction || '일반' }}</td>
              <td>{{ item.routeName }}</td>
              <td class="text-center">{{ item.departureTime.slice(0, 5) }}</td>
              <td class="text-center">
                {{ item.remainingSeats }} / {{ item.totalSeats }}
              </td>
              <td class="text-center">
                <button class="btn btn-primary btn-sm"
                  :disabled="item.remainingSeats === 0"
                  @click="openSeatModal(item)">
                  예약
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 좌석 선택 모달 -->
    <div v-if="modalOpen" class="modal-backdrop-custom" @click.self="closeSeatModal">
      <div class="modal-content-custom">
        <div class="modal-header-custom">
          <h4><strong>좌석 선택</strong></h4>
          <button class="btn-close" @click="closeSeatModal"></button>
        </div>
        <div class="modal-body-custom">
          <p class="mb-3">
            <strong>{{ selectedSchedule?.routeName }}</strong><br>
            {{ selectedDate }} {{ selectedSchedule?.departureTime.slice(0, 5) }} 출발
          </p>

          <div class="seat-grid">
            <button v-for="seat in seats" :key="seat.seatId"
              class="seat-btn"
              :class="{
                'seat-reserved': seat.reserved,
                'seat-selected': selectedSeatId === seat.seatId
              }"
              :disabled="seat.reserved"
              @click="selectedSeatId = seat.seatId">
              {{ seat.seatNum }}
            </button>
          </div>

          <div class="mt-3 d-flex gap-3 small">
            <span><span class="legend legend-available"></span> 선택 가능</span>
            <span><span class="legend legend-reserved"></span> 예약됨</span>
            <span><span class="legend legend-selected"></span> 내 선택</span>
          </div>
        </div>
        <div class="modal-footer-custom">
          <button class="btn btn-secondary" @click="closeSeatModal">취소</button>
          <button class="btn btn-primary" :disabled="!selectedSeatId || submitting"
            @click="submitReservation">
            {{ submitting ? '예약 중...' : '예약 확정' }}
          </button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { shuttleApi } from '@/api/shuttle'
import { reservationApi } from '@/api/reservation'

// ===== 날짜 =====
const today = new Date().toISOString().split('T')[0]
const selectedDate = ref('')

// ===== 셔틀/시간표 목록 =====
const scheduleList = ref([])
const loading = ref(false)
const errorMessage = ref('')

// ===== 좌석 선택 모달 =====
const modalOpen = ref(false)
const selectedSchedule = ref(null)
const seats = ref([])
const selectedSeatId = ref(null)
const submitting = ref(false)

// ===== 시외 셔틀 로드 =====
async function loadShuttleList() {
  if (!selectedDate.value) return

  loading.value = true
  errorMessage.value = ''
  scheduleList.value = []

  try {
    // 1. 시외 셔틀 목록
    const shuttlesRes = await shuttleApi.getAll('INTERCITY')
    const intercityShuttles = shuttlesRes.data.data

    // 2. 각 셔틀별 노선 → 각 노선별 스케줄 수집
    const all = []
    for (const shuttle of intercityShuttles) {
      const routesRes = await shuttleApi.getRoutesByShuttle(shuttle.shuttleId)
      const routes = routesRes.data.data

      for (const route of routes) {
        const schedulesRes = await shuttleApi.getSchedulesByRoute(route.routeId)
        const schedules = schedulesRes.data.data

        // 각 스케줄의 잔여 좌석 조회 (intercity_shuttle_id 찾기 위해 추가 단계 필요)
        for (const sched of schedules) {
          // 잔여 좌석 조회
          const intercityShuttleId = await findIntercityShuttleId(shuttle.shuttleId)
          if (!intercityShuttleId) continue

          const seatsRes = await reservationApi.getAvailableSeats(
            intercityShuttleId, sched.scheduleId, selectedDate.value
          )
          const seatList = seatsRes.data.data
          const remaining = seatList.filter((s) => !s.reserved).length

          all.push({
            scheduleId: sched.scheduleId,
            routeId: route.routeId,
            routeName: route.routeName,
            direction: route.direction,
            departureTime: sched.departureTime,
            dayOfWeek: sched.dayOfWeek,
            shuttleId: shuttle.shuttleId,
            shuttleName: shuttle.name,
            intercityShuttleId,
            remainingSeats: remaining,
            totalSeats: seatList.length
          })
        }
      }
    }
    scheduleList.value = all
  } catch (error) {
    errorMessage.value = error.response?.data?.error?.message || '셔틀 정보를 불러오는데 실패했습니다.'
  } finally {
    loading.value = false
  }
}

// intercity_shuttle_id를 shuttle_id 기반으로 찾는 헬퍼
// 현재 Backend에 직접 매핑 API가 없어서 /api/reservations/seats 호출로 좌석이 있으면 추론
// 대체안: Backend에 endpoint 추가. 지금은 shuttle_id 순서 기반 간단 매핑 (시내1, 시외2면 intercity=1)
const intercityIdCache = {}
async function findIntercityShuttleId(shuttleId) {
  if (intercityIdCache[shuttleId] !== undefined) return intercityIdCache[shuttleId]

  // 휴리스틱: shuttle_id - 시내 셔틀 수 = intercity_shuttle_id
  // 또는: shuttleId=2 → intercity_shuttle_id=1 (시외셔틀 잠실)
  // Backend에 /api/shuttles/{id}/intercity 엔드포인트를 추가하면 정확해짐.
  // 지금은 단순화: 좌석 1건 조회 시도하여 존재 여부 확인
  for (let guessId = 1; guessId <= 10; guessId++) {
    try {
      const res = await reservationApi.getAvailableSeats(guessId, 1, '2000-01-01')
      if (res.data.data.length > 0) {
        intercityIdCache[shuttleId] = guessId
        return guessId
      }
    } catch (e) {
      continue
    }
  }
  intercityIdCache[shuttleId] = null
  return null
}

// ===== 좌석 모달 =====
async function openSeatModal(schedule) {
  selectedSchedule.value = schedule
  selectedSeatId.value = null
  modalOpen.value = true

  try {
    const res = await reservationApi.getAvailableSeats(
      schedule.intercityShuttleId, schedule.scheduleId, selectedDate.value
    )
    seats.value = res.data.data
  } catch (error) {
    alert(error.response?.data?.error?.message || '좌석 정보를 불러오는데 실패했습니다.')
    modalOpen.value = false
  }
}

function closeSeatModal() {
  modalOpen.value = false
  selectedSchedule.value = null
  seats.value = []
  selectedSeatId.value = null
}

async function submitReservation() {
  if (!selectedSeatId.value || !selectedSchedule.value) return

  submitting.value = true
  try {
    await reservationApi.create({
      intercityShuttleId: selectedSchedule.value.intercityShuttleId,
      seatId: selectedSeatId.value,
      scheduleId: selectedSchedule.value.scheduleId,
      travelDate: selectedDate.value
    })
    alert('예약이 완료되었습니다!')
    closeSeatModal()
    loadShuttleList()
  } catch (error) {
    alert(error.response?.data?.error?.message || '예약에 실패하였습니다.')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  selectedDate.value = today
  loadShuttleList()
})
</script>

<style scoped>
.modal-backdrop-custom {
  position: fixed; top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(0, 0, 0, 0.5); z-index: 2000;
  display: flex; justify-content: center; align-items: center;
}
.modal-content-custom {
  background: #fff; border-radius: 10px; width: 500px; max-width: 90%;
  max-height: 85vh; overflow-y: auto;
}
.modal-header-custom, .modal-footer-custom {
  padding: 20px; display: flex; align-items: center;
}
.modal-header-custom {
  justify-content: space-between; border-bottom: 1px solid #dee2e6;
}
.modal-footer-custom {
  justify-content: flex-end; gap: 10px; border-top: 1px solid #dee2e6;
}
.modal-body-custom { padding: 20px; }

.seat-grid {
  display: grid; grid-template-columns: repeat(4, 1fr); gap: 10px;
}
.seat-btn {
  padding: 12px; border: 2px solid #1A76D1; background: white;
  color: #1A76D1; border-radius: 6px; font-weight: bold;
  cursor: pointer; transition: all 0.2s;
}
.seat-btn:hover:not(:disabled) { background: #e3f2fd; }
.seat-btn.seat-selected { background: #1A76D1; color: white; }
.seat-btn.seat-reserved {
  background: #adb5bd; border-color: #adb5bd; color: white;
  cursor: not-allowed; opacity: 0.7;
}

.legend {
  display: inline-block; width: 16px; height: 16px;
  border-radius: 3px; vertical-align: middle; margin-right: 4px;
}
.legend-available { border: 2px solid #1A76D1; }
.legend-reserved { background: #adb5bd; }
.legend-selected { background: #1A76D1; }
</style>