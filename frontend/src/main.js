import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

// Bootstrap CSS + JS
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'

// Font Awesome
import '@fortawesome/fontawesome-free/css/all.min.css'

// 커스텀 스타일
import './assets/css/styles.css'

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')