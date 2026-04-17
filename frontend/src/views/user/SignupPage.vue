<template>
  <div class="container py-5 d-flex justify-content-center">
    <div class="col-md-8 col-lg-6">
      <h1 class="text-center mb-2">회원가입</h1>
      <p class="text-center mb-4 section-subtitle">Sign up</p>

      <form @submit.prevent="handleSignup" class="p-4 border rounded bg-light shadow-sm">
        <div class="mb-3">
          <label for="name" class="form-label">이름</label>
          <input type="text" id="name" v-model="name" class="form-control"
            required placeholder="이름을 입력하세요" />
        </div>

        <div class="mb-3">
          <label for="email" class="form-label">이메일</label>
          <input type="email" id="email" v-model="email" class="form-control"
            required placeholder="이메일을 입력하세요" />
        </div>

        <div class="mb-3">
          <label for="password" class="form-label">비밀번호</label>
          <input type="password" id="password" v-model="password" class="form-control"
            required placeholder="비밀번호 (8자 이상)" minlength="8" />
        </div>

        <div class="mb-3">
          <label for="passwordConfirm" class="form-label">비밀번호 확인</label>
          <input type="password" id="passwordConfirm" v-model="passwordConfirm"
            class="form-control" required placeholder="비밀번호를 다시 입력하세요" />
        </div>

        <div class="mb-3">
          <button type="submit" class="btn btn-primary w-100" :disabled="loading">
            {{ loading ? '회원가입 중...' : '회원가입' }}
          </button>
        </div>

        <div v-if="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>
      </form>

      <div class="text-center mt-3">
        <p>
          이미 계정이 있으신가요?
          <router-link to="/login" class="text-primary fw-bold">로그인</router-link>
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

const name = ref('')
const email = ref('')
const password = ref('')
const passwordConfirm = ref('')
const loading = ref(false)
const errorMessage = ref('')

async function handleSignup() {
  errorMessage.value = ''

  if (password.value !== passwordConfirm.value) {
    errorMessage.value = '비밀번호가 일치하지 않습니다.'
    return
  }

  loading.value = true

  try {
    await authStore.signup(name.value, email.value, password.value)
    alert('회원가입이 완료되었습니다. 로그인해주세요.')
    router.push('/login')
  } catch (error) {
    const msg = error.response?.data?.error?.message || '회원가입에 실패하였습니다.'
    errorMessage.value = msg
  } finally {
    loading.value = false
  }
}
</script>