<script setup lang="ts">
import Button from 'primevue/button'
</script>

<template>
  <div>
    <h1>DepView</h1>
  </div>
  <router-link v-slot="{ href, navigate }" to="/deployments/new">
    <a :href="href" @click="navigate">
      <Button icon="pi pi-plus" severity="primary" />
    </a>
  </router-link>
</template>
