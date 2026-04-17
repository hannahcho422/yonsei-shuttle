import api from './index'

export const locationApi = {
  getAllLocations() {
    return api.get('/shuttles/locations')
  },
  getLocation(shuttleId) {
    return api.get(`/shuttles/${shuttleId}/location`)
  },
  getEta(shuttleId, routeId) {
    return api.get(`/shuttles/${shuttleId}/eta`, { params: { routeId } })
  }
}