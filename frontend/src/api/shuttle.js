import api from './index'

export const shuttleApi = {
  // 셔틀
  getAll(type) {
    return api.get('/shuttles', { params: type ? { type } : {} })
  },
  getOne(shuttleId) {
    return api.get(`/shuttles/${shuttleId}`)
  },
  // 노선
  getAllRoutes() {
    return api.get('/shuttles/routes')
  },
  getRoutesByShuttle(shuttleId) {
    return api.get(`/shuttles/${shuttleId}/routes`)
  },
  getRoute(routeId) {
    return api.get(`/shuttles/routes/${routeId}`)
  },
  // 정류장
  getAllStops() {
    return api.get('/shuttles/stops')
  },
  getStopsByRoute(routeId) {
    return api.get(`/shuttles/routes/${routeId}/stops`)
  },
  // 시간표
  getSchedulesByRoute(routeId) {
    return api.get(`/shuttles/routes/${routeId}/schedules`)
  },
  getSchedulesByRouteAndDay(routeId, dayOfWeek) {
    return api.get(`/shuttles/routes/${routeId}/schedules/${dayOfWeek}`)
  }
}