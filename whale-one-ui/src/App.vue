<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
import HelloWorld from './components/HelloWorld.vue'
import Toast from 'primevue/toast';
import ConfirmDialog from "primevue/confirmdialog";
import TheNav from '@/components/TheNav.vue'
</script>

<template>
<!--  <header>-->
  <!--    <img alt="Vue logo" class="logo" src="@/assets/logo.svg" width="125" height="125" />-->

  <!--    <div class="wrapper">-->
  <!--      <HelloWorld msg="You did it!" />-->

  <!--      <nav>-->
  <!--        <RouterLink to="/">Home</RouterLink>-->
  <!--        <RouterLink to="/about">About</RouterLink>-->
  <!--      </nav>-->
  <!--    </div>-->
  <!--  </header>-->
  <Toast position="top-center"/>
  <ConfirmDialog/>
  <TheNav />
  <RouterView />
</template>

<style scoped>

</style>
