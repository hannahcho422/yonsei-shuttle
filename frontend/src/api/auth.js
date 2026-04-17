import api from './index'

export const authApi = {
  signup(data) {
    return api.post('/auth/signup', data)
  },
  login(data) {
    return api.post('/auth/login', data)
  },
  refresh(refreshToken) {
    return api.post('/auth/refresh', null, {
      headers: { 'Refresh-Token': refreshToken }
    })
  }
}