<script setup lang="ts">
import { onMounted, ref, type Ref } from 'vue'

import Button from 'primevue/button'
import Checkbox from 'primevue/checkbox'
import Column from 'primevue/column'
import DataTable from 'primevue/datatable'
import InputText from 'primevue/inputtext'
import Popover from 'primevue/popover'
import { FilterMatchMode } from '@primevue/core/api'

import { invokeEquipmentTypeListGet } from '@/client/equipmentTypeClient.ts'

import type { Filter, PageModel } from '@/model/BaseModel.ts'
import type { EquipmentTypeModel } from '@/model/EquipmentTypeModel.ts'

const list: Ref<PageModel<EquipmentTypeModel> | null> = ref(null)

const loading = ref(false)
const pageSize = ref(10)

const loadPage = (page: number, size: number) => {
  loading.value = true
  invokeEquipmentTypeListGet(page, size)
    .then((data) => (list.value = data))
    .catch(() => {})
    .finally(() => (loading.value = false))
}

const filters: Ref<Filter> = ref({})

const initFilters = () => {
  filters.value = {
    name: { value: null, matchMode: FilterMatchMode.CONTAINS },
    isAssembly: { value: null, matchMode: FilterMatchMode.EQUALS },
  }
}

initFilters()

const resetFilters = () => {
  initFilters()
  loadPage(0, pageSize.value)
}

const settingsPopover = ref()

const toggleSettingsPopover = (event: Event) => {
  settingsPopover.value.toggle(event)
}

const isIdVisible = ref(false)

onMounted(() => {
  loadPage(0, pageSize.value)
})
</script>

<template>
  <Popover ref="settingsPopover">
    <div class="flex flex-col gap-4 w-40">
      <div class="flex items-center gap-2">
        <Checkbox
          v-model="isIdVisible"
          binary
        />
        <label>Show Id</label>
      </div>
    </div>
  </Popover>

  <DataTable
    v-model:filters="filters"
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
    row-hover
    @page="loadPage($event.page, $event.rows)"
    @filter="loadPage(0, $event.rows)"
  >
    <template #header>
      <div class="flex flex-wrap items-center gap-2">
        <span class="text-xl font-bold flex-grow">Equipment Types</span>
        <Button
          icon="pi pi-filter-slash"
          title="Clear Filters"
          severity="secondary"
          @click="resetFilters()"
        />
        <Button
          icon="pi pi-cog"
          title="Settings"
          severity="secondary"
          @click="toggleSettingsPopover"
        />
        <router-link
          v-slot="{ href, navigate }"
          to="/administration/equipment/types/new"
        >
          <a
            :href="href"
            @click="navigate"
          >
            <Button
              icon="pi pi-plus"
              title="Create New"
              severity="primary"
            />
          </a>
        </router-link>
      </div>
    </template>

    <Column
      v-if="isIdVisible"
      field="id"
      header="Id"
      class="w-1/12"
    />

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
      field="isAssembly"
      header="Assembly"
      class="w-1/12"
      :show-filter-match-modes="false"
      :show-apply-button="false"
    >
      <template #filter="{ filterModel, filterCallback }">
        <Checkbox
          v-model="filterModel.value"
          binary
          @change="filterCallback()"
        />
      </template>
      <template #body="slotProps">
        <i v-if="slotProps.data.isAssembly" class="pi pi-check"/>
      </template>
    </Column>

    <Column
      header="Actions"
      class="w-1/12"
    >
      <template #body="slotProps">
        <router-link
          v-slot="{ href, navigate }"
          :to="`/administration/equipment/types/${slotProps.data.id}`"
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

<style scoped>
:deep(.p-datatable-header-cell:hover) {
  background-color: var(--p-surface-50);
}
</style>
