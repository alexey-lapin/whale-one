<script setup lang="ts">
import { onMounted, type Ref, ref } from 'vue'

import Button from 'primevue/button'
import Column from 'primevue/column'
import DataTable from 'primevue/datatable'

import { invokeProjectListGet } from '@/client/projectClient.ts'

import type { FilterConditions, PageModel } from '@/model/BaseModel.ts'
import type { ProjectModel } from '@/model/ProjectModel.ts'
import { FilterMatchMode } from '@primevue/core/api'
import InputText from 'primevue/inputtext'
import Checkbox from 'primevue/checkbox'
import Popover from 'primevue/popover'

const list: Ref<PageModel<ProjectModel> | null> = ref(null)

const loading = ref(false)
const pageSize = ref(10)

const loadPage = (page: number, size: number) => {
  loading.value = true
  invokeProjectListGet(page, size)
    .then((data) => (list.value = data))
    .catch(() => {})
    .finally(() => (loading.value = false))
}

const filters: Ref<FilterConditions> = ref({})

const initFilters = () => {
  filters.value = {
    name: { value: null, matchMode: FilterMatchMode.CONTAINS },
    client: { value: null, matchMode: FilterMatchMode.CONTAINS },
    ownership: { value: null, matchMode: FilterMatchMode.CONTAINS },
    region: { value: null, matchMode: FilterMatchMode.CONTAINS },
    type: { value: null, matchMode: FilterMatchMode.CONTAINS },
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
    :value="list?.items"
    paginator
    :total-records="list?.totalElements"
    :rows="pageSize"
    :rows-per-page-options="[5, 10, 20, 50]"
    :loading="loading"
    size="small"
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
          icon="pi pi-filter-slash"
          severity="secondary"
          @click="resetFilters()"
        />
        <Button
          icon="pi pi-cog"
          severity="secondary"
          @click="toggleSettingsPopover"
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
      v-if="isIdVisible"
      field="id"
      header="Id"
      class="w-1/12"
    />

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
</template>
