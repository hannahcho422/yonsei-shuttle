import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/api'

export const useAuthStore = defineStore('auth', () => {
  const accessToken = ref(localStorage.getItem('accessToken') || '')
  const refreshToken = ref(localStorage.getItem('refreshToken') || '')
  const user = ref(null)

  const isLoggedIn = computed(() => !!accessToken.value)
  const userName = computed(() => user.value?.name || '')
  const userRole = computed(() => user.value?.role || '')
  const isAdmin = computed(() => userRole.value === 'ADMIN')

  // 회원가입
  async function signup(name, email, password) {
    const response = await api.post('/auth/signup', { name, email, password })
    return response.data.data
  }

  // 로그인
  async function login(email, password) {
    const response = await api.post('/auth/login', { email, password })
    const data = response.data.data

    accessToken.value = data.accessToken
    refreshToken.value = data.refreshToken
    localStorage.setItem('accessToken', data.accessToken)
    localStorage.setItem('refreshToken', data.refreshToken)

    // 토큰에서 사용자 정보 디코딩
    decodeToken(data.accessToken)

    return data
  }

  // 로그아웃
  function logout() {
    accessToken.value = ''
    refreshToken.value = ''
    user.value = null
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
  }

  // 앱 로드 시 로그인 상태 확인
  function checkLoginStatus() {
    const token = localStorage.getItem('accessToken')
    if (token) {
      try {
        decodeToken(token)
      } catch (e) {
        logout()
      }
    }
  }

  // JWT 페이로드 디코딩 (서명 검증 없이 클레임만 추출)
  function decodeToken(token) {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]))

      // 만료 체크
      if (payload.exp * 1000 < Date.now()) {
        logout()
        return
      }

      user.value = {
        userId: parseInt(payload.sub),
        email: payload.email,
        role: payload.role,
        name: payload.email.split('@')[0] // 임시: 이름은 이메일에서 추출
      }
    } catch (e) {
      logout()
    }
  }

  return {
    accessToken,
    refreshToken,
    user,
    isLoggedIn,
    userName,
    userRole,
    isAdmin,
    signup,
    login,
    logout,
    checkLoginStatus
  }
})