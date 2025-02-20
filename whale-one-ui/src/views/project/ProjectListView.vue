<script setup lang="ts">
import { onMounted, type Ref, ref } from 'vue'

import Button from 'primevue/button'
import Column from 'primevue/column'
import DataTable from 'primevue/datatable'

import { invokeProjectListGet } from '@/client/projectClient.ts'

import type { PageModel } from '@/model/BaseModel.ts'
import type { ProjectModel } from '@/model/ProjectModel.ts'
import { FilterMatchMode } from '@primevue/core/api'
import InputText from 'primevue/inputtext'

const projects: Ref<PageModel<ProjectModel> | null> = ref(null)

const loading = ref(false)
const pageSize = ref(10)

const loadPage = (page: number, size: number) => {
  loading.value = true
  invokeProjectListGet(page, size)
    .then((data) => (projects.value = data))
    .catch(() => {})
    .finally(() => (loading.value = false))
}

const filters = ref({
  name: { value: null, matchMode: FilterMatchMode.CONTAINS },
  client: { value: null, matchMode: FilterMatchMode.CONTAINS },
  ownership: { value: null, matchMode: FilterMatchMode.CONTAINS },
  region: { value: null, matchMode: FilterMatchMode.CONTAINS },
  type: { value: null, matchMode: FilterMatchMode.CONTAINS },
})

onMounted(() => {
  loadPage(0, pageSize.value)
})
</script>

<template>
  <div>
    <div>
      <DataTable
        :value="projects?.items"
        paginator
        :total-records="projects?.totalElements"
        :rows="pageSize"
        :rows-per-page-options="[5, 10, 20, 50]"
        :loading="loading"
        row-hover
        filter-display="menu"
        v-model:filters="filters"
        lazy
        current-page-report-template="{first} to {last} of {totalRecords}"
        @page="loadPage($event.page, $event.rows)"
      >
        <template #header>
          <div class="flex flex-wrap items-center gap-2">
            <span class="text-xl font-bold flex-grow">Projects</span>
            <Button
              icon="pi pi-refresh"
              severity="secondary"
            />
            <router-link
              v-slot="{ href, navigate }"
              to="/projects/new"
            >
              <a
                :href="href"
                @click="navigate"
              >
                <Button
                  icon="pi pi-plus"
                  severity="primary"
                />
              </a>
            </router-link>
          </div>
        </template>
        <Column
          field="id"
          header="Id"
          class="w-1/12"
        ></Column>
        <Column
          field="name"
          header="Name"
          class="w-2/12"
          filter-field="name"
          :show-filter-match-modes="false"
          :show-apply-button="false"
        >
          <template #filter="{ filterModel, filterCallback }">
            <InputText
              v-model="filterModel.value"
              @change="filterCallback()"
            />
          </template>
        </Column>
        <Column
          field="client"
          header="Client"
          class="w-2/12"
          :show-filter-match-modes="false"
          :show-apply-button="false"
        >
          <template #filter="{ filterModel, filterCallback }">
            <InputText
              v-model="filterModel.value"
              @change="filterCallback()"
            />
          </template>
        </Column>
        <Column
          field="ownership"
          header="Ownership"
          class="w-2/12"
          :show-filter-match-modes="false"
          :show-apply-button="false"
        >
          <template #filter="{ filterModel, filterCallback }">
            <InputText
              v-model="filterModel.value"
              @change="filterCallback()"
            />
          </template>
        </Column>
        <Column
          field="region"
          header="Region"
          class="w-2/12"
          :show-filter-match-modes="false"
          :show-apply-button="false"
        >
          <template #filter="{ filterModel, filterCallback }">
            <InputText
              v-model="filterModel.value"
              @change="filterCallback()"
            />
          </template>
        </Column>
        <Column
          field="type"
          header="Type"
          class="w-2/12"
          :show-filter-match-modes="false"
          :show-apply-button="false"
        >
          <template #filter="{ filterModel, filterCallback }">
            <InputText
              v-model="filterModel.value"
              @change="filterCallback()"
            />
          </template>
        </Column>
        <Column
          header="Actions"
          class="w-1/12"
        >
          <template #body="slotProps">
            <router-link
              v-slot="{ href, navigate }"
              :to="`/projects/${slotProps.data.id}`"
            >
              <a
                :href="href"
                @click="navigate"
              >
                <Button
                  label="Edit"
                  size="small"
                  variant="outlined"
                  severity="secondary"
                />
              </a>
            </router-link>
          </template>
        </Column>
      </DataTable>
    </div>
  </div>
</template>

<style scoped>
:deep(.p-datatable-header-cell:hover) {
  background-color: var(--p-surface-50);
}
</style>
