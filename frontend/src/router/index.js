import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: () => import('@/components/common/LayoutComponent.vue'),
      children: [
        {
          path: '',
          name: 'Home',
          component: () => import('@/views/user/HomePage.vue')
        },
        // Auth
        {
          path: '/login',
          name: 'Login',
          component: () => import('@/views/user/LoginPage.vue')
        },
        {
          path: '/signup',
          name: 'Signup',
          component: () => import('@/views/user/SignupPage.vue')
        },
        // Notification
        {
          path: '/notifications',
          name: 'Notifications',
          component: () => import('@/views/user/NotificationPage.vue')
        },
        {
          path: '/notifications/:id',
          name: 'NotificationDetail',
          component: () => import('@/views/user/NotificationDetail.vue')
        },
        // Shuttle
        {
          path: '/routes',
          name: 'Routes',
          component: () => import('@/views/user/RoutePage.vue')
        },
        {
          path: '/routes/:routeId/stops',
          name: 'RouteStops',
          component: () => import('@/views/user/RouteStopPage.vue')
        },
        {
          path: '/routes/:routeId/schedules',
          name: 'RouteSchedules',
          component: () => import('@/views/user/SchedulePage.vue')
        },
        {
          path: '/stops',
          name: 'Stops',
          component: () => import('@/views/user/StopPage.vue')
        },
        // Reservation
        {
          path: '/reservation',
          name: 'Reservation',
          meta: { requiresAuth: true },
          component: () => import('@/views/user/ReservationPage.vue')
        },
        {
          path: '/reservation/history',
          name: 'ReservationHistory',
          meta: { requiresAuth: true },
          component: () => import('@/views/user/ReservationHistory.vue')
        },
        // Location
        {
          path: '/live-map',
          name: 'LiveMap',
          component: () => import('@/views/user/LiveMapPage.vue')
        }
      ]
    }
  ]
})

// 네비게이션 가드
router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth) {
    const token = localStorage.getItem('accessToken')
    if (!token) {
      next({ name: 'Login' })
      return
    }
  }
  next()
})

export default router