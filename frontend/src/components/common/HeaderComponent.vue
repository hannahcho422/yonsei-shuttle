<template>
    <nav class="navbar navbar-expand-lg custom-navbar fixed-top">
      <div class="container-fluid">
        <router-link to="/" class="navbar-brand company-name">
          연세셔틀
        </router-link>
  
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
          data-bs-target="#navbarCollapse" aria-controls="navbarCollapse"
          aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>
  
        <div class="collapse navbar-collapse" id="navbarCollapse">
          <div class="navbar-nav ms-auto">
  
            <!-- 공지사항 -->
            <router-link to="/notifications" class="nav-item nav-link">공지사항</router-link>
  
            <!-- 노선 Dropdown -->
            <div class="nav-item dropdown">
              <a class="nav-link dropdown-toggle" href="#" role="button"
                data-bs-toggle="dropdown" aria-expanded="false">
                노선 <i class="fas fa-caret-down ms-1"></i>
              </a>
              <ul class="dropdown-menu">
                <li><router-link to="/routes?type=CITY" class="dropdown-item">시내 셔틀</router-link></li>
                <li><router-link to="/routes?type=INTERCITY" class="dropdown-item">시외 셔틀</router-link></li>
              </ul>
            </div>
  
            <!-- 정류장 -->
            <router-link to="/stops" class="nav-item nav-link">승하차 장소</router-link>
  
            <!-- 실시간 위치 -->
            <router-link to="/live-map" class="nav-item nav-link">실시간 위치</router-link>
  
            <!-- 예약 Dropdown -->
            <div class="nav-item dropdown">
              <a class="nav-link dropdown-toggle" href="#" role="button"
                data-bs-toggle="dropdown" aria-expanded="false">
                예약 <i class="fas fa-caret-down ms-1"></i>
              </a>
              <ul class="dropdown-menu">
                <li><router-link to="/reservation" class="dropdown-item">시외 셔틀 예약</router-link></li>
                <li><router-link to="/reservation/history" class="dropdown-item">예약 내역/취소</router-link></li>
              </ul>
            </div>
  
            <!-- 로그인 / 사용자 -->
            <template v-if="authStore.isLoggedIn">
              <div class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="#" role="button"
                  data-bs-toggle="dropdown" aria-expanded="false">
                  {{ authStore.userName }}
                </a>
                <ul class="dropdown-menu">
                  <li><button @click="handleLogout" class="dropdown-item">로그아웃</button></li>
                </ul>
              </div>
            </template>
            <router-link v-else to="/login" class="nav-item nav-link">로그인</router-link>
  
          </div>
        </div>
      </div>
    </nav>
  </template>
  
  <script setup>
  import { useRouter } from 'vue-router'
  import { useAuthStore } from '@/stores/auth'
  
  const router = useRouter()
  const authStore = useAuthStore()
  
  function handleLogout() {
    authStore.logout()
    router.push('/')
  }
  </script>