<script setup lang="ts">
import { onMounted, ref, type Ref } from 'vue'

import Button from 'primevue/button'
import Column from 'primevue/column'
import DataTable from 'primevue/datatable'

import type { EquipmentTypeModel } from '@/model/EquipmentTypeModel.ts'
import type PageModel from '@/model/PageModel.ts'

const list: Ref<PageModel<EquipmentTypeModel> | null> = ref(null)

const loading = ref(false)
const pageSize = ref(10)

onMounted(() => {
  loadPage(0, pageSize.value)
})

const loadPage = (page: number, size: number) => {
  loading.value = true
  fetch(`/api/equipment/types?page=${page}&size=${size}`)
    .then((response) => response.json())
    .then((data) => (list.value = data))
    .catch((error) => console.error(error))
    .finally(() => (loading.value = false))
}
</script>

<template>
  <div>
    <div>
      <DataTable
        :value="list?.items"
        :total-records="list?.totalElements"
        :rows="pageSize"
        :rowsPerPageOptions="[5, 10, 20, 50]"
        :loading="loading"
        currentPageReportTemplate="{first} to {last} of {totalRecords}"
        lazy
        paginator
        @page="loadPage($event.page, $event.rows)"
      >
        <template #header>
          <div class="flex flex-wrap items-center gap-2">
            <span class="text-xl font-bold flex-grow">Equipment Types</span>
            <Button
              icon="pi pi-refresh"
              severity="secondary"
            />
            <router-link
              v-slot="{ href, navigate }"
              to="/equipment/types/new"
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
          style="width: 10%"
        ></Column>
        <Column
          field="name"
          header="Name"
          style="width: 80%"
        ></Column>
        <Column style="width: 10%">
          <template #body="slotProps">
            <router-link
              v-slot="{ href, navigate }"
              :to="`/equipment/types/${slotProps.data.id}`"
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
