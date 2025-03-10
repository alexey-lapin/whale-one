<script setup lang="ts">
import { onMounted, ref } from 'vue'
import L from 'leaflet'

const mapContainer = ref<HTMLDivElement | null>(null)

onMounted(() => {
  if (!mapContainer.value) {
    return
  }

  const map = L.map(mapContainer.value).setView([-31.9816, 115.8275], 5)

  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
  }).addTo(map)

  // L.marker([35.7796, -78.6382]).addTo(map).bindPopup('Apex, NC').openPopup()
})
</script>

<template>
  <div
    ref="mapContainer"
    class="mt-2 w-full h-96"
  ></div>
</template>

<style scoped>
.map-container {
  width: 100%;
  height: 100%;
}
</style>
