import api from './index'

export const notificationApi = {
  getAll() {
    return api.get('/notifications')
  },
  getOne(notificationId) {
    return api.get(`/notifications/${notificationId}`)
  }
}