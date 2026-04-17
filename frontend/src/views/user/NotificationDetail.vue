<template>
  <section>
    <div class="container mt-5" style="padding-top: 30px; padding-bottom: 30px;">
      <div class="section-header text-left">
        <h1 class="mb-2">공지사항</h1>
        <p class="section-subtitle">Notification Detail</p>
      </div>

      <div v-if="loading" class="text-center py-5">
        <div class="spinner-border text-primary"></div>
      </div>

      <div v-else-if="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>

      <div v-else-if="notification" class="border rounded p-4 bg-white shadow-sm">
        <h3 class="mb-3" style="color: #3366CC; font-weight: bold;">
          {{ notification.title }}
        </h3>
        <div class="d-flex justify-content-between text-muted mb-4 pb-3"
          style="border-bottom: 1px solid #e0e0e0;">
          <span>작성자: {{ notification.adminName }}</span>
          <span>{{ formatDate(notification.createdAt) }}</span>
        </div>
        <div style="min-height: 200px; white-space: pre-wrap; line-height: 1.8;">
          {{ notification.content }}
        </div>
      </div>

      <div class="mt-4">
        <router-link to="/notifications" class="btn btn-outline-primary">
          <i class="fa fa-arrow-left me-2"></i>목록으로
        </router-link>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { notificationApi } from '@/api/notification'

const route = useRoute()
const notification = ref(null)
const loading = ref(true)
const errorMessage = ref('')

onMounted(async () => {
  try {
    const response = await notificationApi.getOne(route.params.id)
    notification.value = response.data.data
  } catch (error) {
    errorMessage.value = error.response?.data?.error?.message || '공지사항을 불러오는데 실패했습니다.'
  } finally {
    loading.value = false
  }
})

function formatDate(dateString) {
  const date = new Date(dateString)
  return date.toLocaleString('ko-KR', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit'
  })
}
</script>