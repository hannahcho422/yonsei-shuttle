<template>
  <div>
    <HeaderComponent />
    <main>
      <router-view />
    </main>
    <FooterComponent />
  </div>
</template>

<script setup>
import HeaderComponent from './HeaderComponent.vue'
import FooterComponent from './FooterComponent.vue'
</script>