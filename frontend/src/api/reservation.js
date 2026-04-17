import api from './index'

export const reservationApi = {
  create(data) {
    return api.post('/reservations', data)
  },
  getMyList() {
    return api.get('/reservations')
  },
  getOne(reservationId) {
    return api.get(`/reservations/${reservationId}`)
  },
  cancel(reservationId) {
    return api.patch(`/reservations/${reservationId}/cancel`)
  },
  getAvailableSeats(intercityShuttleId, scheduleId, travelDate) {
    return api.get('/reservations/seats', {
      params: { intercityShuttleId, scheduleId, travelDate }
    })
  }
}