<script setup lang="ts">
import { onMounted, ref, type Ref, toRef } from 'vue'

import Button from 'primevue/button'
import Checkbox from 'primevue/checkbox'
import Column from 'primevue/column'
import DataTable from 'primevue/datatable'
import InputText from 'primevue/inputtext'
import Popover from 'primevue/popover'
import Select from 'primevue/select'
import { FilterMatchMode } from '@primevue/core/api'

import { invokeEquipmentTypeListGet } from '@/client/equipmentTypeClient.ts'
import { useListViewStore } from '@/stores/listView.ts'

import type { FilterConditions, PageModel } from '@/model/BaseModel.ts'
import type { EquipmentTypeModel } from '@/model/EquipmentTypeModel.ts'

const listViewStore = useListViewStore()
const listViewConfig = toRef(() => listViewStore.state.equipmentTypes)

const list: Ref<PageModel<EquipmentTypeModel> | null> = ref(null)
const firstRef = ref(0)

const loading = ref(false)

const loadPage = (first: number, page: number, size: number) => {
  loading.value = true
  invokeEquipmentTypeListGet(page, size, filters.value)
    .then((data) => (list.value = data))
    .then(() => (firstRef.value = first))
    .catch(() => {})
    .finally(() => (loading.value = false))
}

const filters: Ref<FilterConditions> = ref({})

const initFilters = () => {
  filters.value = {
    name: { value: null, matchMode: FilterMatchMode.CONTAINS },
    isAssembly: { value: null, matchMode: FilterMatchMode.EQUALS },
  }
}

initFilters()

const resetFilters = () => {
  initFilters()
  firstRef.value = 0
  loadPage(0, 0, listViewConfig.value.pageSize)
}

const settingsPopover = ref()

const toggleSettingsPopover = (event: Event) => {
  settingsPopover.value.toggle(event)
}

const isIdVisible = ref(false)

onMounted(() => {
  loadPage(0, 0, listViewConfig.value.pageSize)
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
    :value="list?.items"
    :total-records="list?.totalElements"
    v-model:rows="listViewConfig.pageSize"
    v-model:first="firstRef"
    v-model:filters="filters"
    :loading="loading"
    size="small"
    filter-display="menu"
    paginator
    row-hover
    lazy
    @page="loadPage($event.first, $event.page, $event.rows)"
    @filter="loadPage(0, 0, $event.rows)"
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
        <i
          v-if="slotProps.data.isAssembly"
          class="pi pi-check"
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

    <template #paginatorstart>
      <span class="font-semibold">Count: {{ list?.totalElements }}</span>
    </template>

    <template #paginatorend>
      <Select
        v-model="listViewConfig.pageSize"
        :options="[10, 20, 50, 100]"
        @change="loadPage(0, 0, $event.value)"
      />
    </template>
  </DataTable>
</template>
