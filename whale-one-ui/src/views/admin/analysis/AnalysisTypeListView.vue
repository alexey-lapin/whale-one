<script setup lang="ts">
import { onMounted, ref, type Ref } from 'vue'

import Button from 'primevue/button'
import Column from 'primevue/column'
import DataTable from 'primevue/datatable'
import InputText from 'primevue/inputtext'

import type { AnalysisTypeModel } from '@/model/AnalysisTypeModel.ts'
import { invokeAnalysisTypeListGet } from '@/client/analysisTypeClient.ts'
import type { PageModel } from '@/model/BaseModel.ts'

const list: Ref<PageModel<AnalysisTypeModel> | null> = ref(null)

const loading = ref(false)
const pageSize = ref(10)

const loadPage = (page: number, size: number) => {
  loading.value = true
  invokeAnalysisTypeListGet(page, size)
    .then((data) => (list.value = data))
    .catch(() => {})
    .finally(() => (loading.value = false))
}

onMounted(() => {
  loadPage(0, pageSize.value)
})
</script>

<template>
  <DataTable
    :value="list?.items"
    :total-records="list?.totalElements"
    :rows="pageSize"
    :rowsPerPageOptions="[5, 10, 20, 50]"
    :loading="loading"
    size="small"
    filter-display="menu"
    currentPageReportTemplate="{first} to {last} of {totalRecords}"
    lazy
    paginator
    @page="loadPage($event.page, $event.rows)"
  >
    <template #header>
      <div class="flex flex-wrap items-center gap-2">
        <span class="text-xl font-bold flex-grow">Analysis Types</span>
        <Button
          icon="pi pi-refresh"
          severity="secondary"
        />
        <router-link
          v-slot="{ href, navigate }"
          to="/administration/analysis/types/new"
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
      class="w-10/12"
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
          :to="`/administration/analysis/types/${slotProps.data.id}`"
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
</template>
