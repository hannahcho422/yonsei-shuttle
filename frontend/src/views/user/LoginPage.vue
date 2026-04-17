<template>
  <div class="container py-5 d-flex justify-content-center">
    <div class="col-md-8 col-lg-6">
      <h1 class="text-center mb-2">로그인</h1>
      <p class="text-center mb-4 section-subtitle">Login</p>

      <form @submit.prevent="handleLogin" class="p-4 border rounded bg-light shadow-sm">
        <div class="mb-3">
          <label for="email" class="form-label">이메일</label>
          <input type="email" id="email" v-model="email" class="form-control"
            required placeholder="이메일을 입력하세요" />
        </div>

        <div class="mb-3">
          <label for="password" class="form-label">비밀번호</label>
          <input type="password" id="password" v-model="password" class="form-control"
            required placeholder="비밀번호를 입력하세요" />
        </div>

        <div class="mb-3">
          <button type="submit" class="btn btn-primary w-100" :disabled="loading">
            {{ loading ? '로그인 중...' : '로그인' }}
          </button>
        </div>

        <div v-if="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>
      </form>

      <div class="text-center mt-3">
        <p>
          계정이 없으신가요?
          <router-link to="/signup" class="text-primary fw-bold">회원가입</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const email = ref('')
const password = ref('')
const loading = ref(false)
const errorMessage = ref('')

async function handleLogin() {
  loading.value = true
  errorMessage.value = ''

  try {
    await authStore.login(email.value, password.value)
    alert('로그인에 성공하였습니다.')
    router.push('/')
  } catch (error) {
    const msg = error.response?.data?.error?.message || '로그인에 실패하였습니다.'
    errorMessage.value = msg
  } finally {
    loading.value = false
  }
}
</script>