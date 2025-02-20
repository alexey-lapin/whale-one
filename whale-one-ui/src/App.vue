<script setup lang="ts">
import { RouterView } from 'vue-router'
import Toast from 'primevue/toast';
import ConfirmDialog from "primevue/confirmdialog";
import TheNav from '@/components/TheNav.vue'
import { useAuthStore } from '@/stores/auth.ts'
import { useToast } from 'primevue/usetoast'
import { apiClientContext } from '@/client/baseClient.ts'

const toast = useToast()
apiClientContext.toast = toast

const auth = useAuthStore()

</script>

<template>
  <Toast position="top-center"/>
  <ConfirmDialog/>
  <TheNav v-if="auth.isAuthenticated()"/>
  <RouterView />
</template>

<style scoped>

</style>
