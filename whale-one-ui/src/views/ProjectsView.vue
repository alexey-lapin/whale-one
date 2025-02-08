<script setup lang="ts">
import { onMounted, type Ref, ref } from 'vue'

import Button from 'primevue/button'
import Column from 'primevue/column'
import DataTable from 'primevue/datatable'
import TheNav from '@/components/TheNav.vue'
import type PageModel from '@/model/PageModel.ts'

const projects: Ref<PageModel<Project> | null> = ref(null)

const loading = ref(false)
const pageSize = ref(10)

type Project = {
  id: number,
  version: number,
  created_at: string,
  created_by: string,
  updated_by_id: string,
  name: string,
  description: string,
}

onMounted(() => {
  loadPage(0, pageSize.value)
})

const loadPage = (page: number, size: number) => {
  loading.value = true
  fetch(`/api/projects?page=${page}&size=${size}`)
    .then(response => response.json())
    .then(data => projects.value = data)
    .catch(error => console.error(error))
    .finally(() => loading.value = false)
}
</script>

<template>
<!--  <TheNav />-->
  <div>
<!--    <h1>ProjectView</h1>-->
<!--    <router-link v-slot="{ href, navigate }" to="/projects/new">-->
<!--      <a v-ripple :href="href" @click="navigate">-->
<!--        <Button label="New"></Button>-->
<!--      </a>-->
<!--    </router-link>-->
    <div>
      <!--      <ul>-->
      <!--        <li v-for="project in projects" :key="project.id">-->
      <!--          <div class="flex gap-2">-->
      <!--            <p>{{ project.id }}</p>-->
      <!--            <p>{{ project.name }}</p>-->
      <!--            <router-link v-slot="{ href, navigate }" :to="`/projects/${project.id}`">-->
      <!--              <a v-ripple :href="href" @click="navigate">-->
      <!--                {{ project.name }}-->
      <!--              </a>-->
      <!--            </router-link>-->
      <!--          </div>-->
      <!--        </li>-->
      <!--      </ul>-->
      <DataTable :value="projects?.items"
                 paginator
                 :total-records="projects?.totalElements"
                 :rows="pageSize"
                 :rowsPerPageOptions="[5, 10, 20, 50]"
                 :loading="loading"
                 currentPageReportTemplate="{first} to {last} of {totalRecords}"
                 @page="loadPage($event.page, $event.rows)"
                 lazy>
        <template #header>
          <div class="flex flex-wrap items-center gap-2">
            <span class="text-xl font-bold flex-grow">Projects</span>
            <Button icon="pi pi-refresh" severity="secondary" />
            <router-link v-slot="{ href, navigate }" to="/projects/new">
              <a :href="href" @click="navigate">
                <Button icon="pi pi-plus" severity="primary" />
              </a>
            </router-link>
          </div>
        </template>
        <Column field="id" header="Id" style="width: 10%"></Column>
        <Column field="name" header="Name" style="width: 80%"></Column>
        <Column style="width: 10%">
          <template #body="slotProps">
            <router-link v-slot="{ href, navigate }" :to="`/projects/${slotProps.data.id}`">
              <a :href="href" @click="navigate">
                <Button label="Edit" size="small" variant="outlined" severity="secondary"/>
              </a>
            </router-link>
          </template>
        </Column>
      </DataTable>
    </div>
  </div>
</template>
