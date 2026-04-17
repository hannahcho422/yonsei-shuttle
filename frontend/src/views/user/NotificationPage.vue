<template>
  <section>
    <div class="container mt-5" style="padding-top: 30px; padding-bottom: 30px;">
      <div class="section-header text-left">
        <h1 class="mb-2">공지사항</h1>
        <p class="section-subtitle">Notifications</p>
      </div>

      <!-- 로딩 중 -->
      <div v-if="loading" class="text-center py-5">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>

      <!-- 에러 -->
      <div v-else-if="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>

      <!-- 목록 -->
      <div v-else>
        <div v-if="notifications.length === 0" class="text-center py-5 text-muted">
          공지사항이 없습니다.
        </div>

        <table v-else class="table table-hover">
          <thead class="table-light">
            <tr>
              <th style="width: 10%;" class="text-center">번호</th>
              <th>제목</th>
              <th style="width: 15%;" class="text-center">작성자</th>
              <th style="width: 20%;" class="text-center">작성일</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(notification, index) in notifications"
              :key="notification.notificationId"
              @click="goToDetail(notification.notificationId)"
              style="cursor: pointer;">
              <td class="text-center">{{ notifications.length - index }}</td>
              <td>{{ notification.title }}</td>
              <td class="text-center">{{ notification.adminName }}</td>
              <td class="text-center">{{ formatDate(notification.createdAt) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { notificationApi } from '@/api/notification'

const router = useRouter()
const notifications = ref([])
const loading = ref(true)
const errorMessage = ref('')

onMounted(async () => {
  try {
    const response = await notificationApi.getAll()
    notifications.value = response.data.data
  } catch (error) {
    errorMessage.value = error.response?.data?.error?.message || '공지사항을 불러오는데 실패했습니다.'
  } finally {
    loading.value = false
  }
})

function goToDetail(id) {
  router.push(`/notifications/${id}`)
}

function formatDate(dateString) {
  const date = new Date(dateString)
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric', month: '2-digit', day: '2-digit'
  }).replace(/\. /g, '-').replace('.', '')
}
</script>

<style scoped>
table tbody tr:hover {
  background-color: #f4f9ff;
}
</style>