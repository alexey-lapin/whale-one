<script setup lang="ts">
import { onMounted, ref, type Ref } from 'vue'

import Button from 'primevue/button'
import Column from 'primevue/column'
import DataTable from 'primevue/datatable'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import { FilterMatchMode } from '@primevue/core/api'

import { invokeDeploymentListGet } from '@/client/deploymentClient.ts'

import type { BaseRefModel, FilterConditions, PageModel } from '@/model/BaseModel.ts'
import type { DeploymentModel } from '@/model/DeploymentModel.ts'
import { invokeProjectItemListGet, invokeSiteItemListGet } from '@/client/projectClient.ts'
import AutoComplete from 'primevue/autocomplete'
import Checkbox from 'primevue/checkbox'
import Popover from 'primevue/popover'

const list: Ref<PageModel<DeploymentModel> | null> = ref(null)

const projects: Ref<BaseRefModel[]> = ref([])
const sites: Ref<BaseRefModel[]> = ref([])

const loading = ref(false)
const pageSize = ref(10)

const loadPage = (page: number, size: number) => {
  loading.value = true
  invokeDeploymentListGet(
    page,
    size,
    filters.value['name'].value,
    filters.value['projectId'].value?.id,
    filters.value['projectSiteId'].value?.id,
    filters.value['status'].value,
  )
    .then((data) => (list.value = data))
    .catch(() => {})
    .finally(() => (loading.value = false))
}

const filters: Ref<FilterConditions> = ref({})

const initFilters = () => {
  filters.value = {
    name: { value: null, matchMode: FilterMatchMode.CONTAINS },
    projectId: { value: null, matchMode: FilterMatchMode.EQUALS },
    projectSiteId: { value: null, matchMode: FilterMatchMode.EQUALS },
    status: { value: null, matchMode: FilterMatchMode.IN },
  }
}

initFilters()

const resetFilters = () => {
  initFilters()
  loadPage(0, pageSize.value)
}

const projectItems = (q: string) => {
  invokeProjectItemListGet(q)
    .then((data) => (projects.value = data))
    .catch(() => {})
}

const siteItems = (projectId: number | null, q: string | null) => {
  if (!projectId) {
    return
  }
  invokeSiteItemListGet(projectId, q)
    .then((data) => (sites.value = data))
    .catch(() => {})
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
    v-model:rows="pageSize"
    v-model:filters="filters"
    :total-records="list?.totalElements"
    :loading="loading"
    size="small"
    filter-display="menu"
    paginator
    :rows-per-page-options="[1, 5, 10, 20, 50]"
    paginator-template="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
    row-hover
    lazy
    @page="loadPage($event.page, $event.rows)"
    @filter="loadPage(0, $event.rows)"
  >
    <template #header>
      <div class="flex flex-wrap items-center gap-2">
        <span class="text-xl font-bold grow">Deployments</span>
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
          to="/deployments/new"
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
    >
    </Column>

    <Column
      field="name"
      header="Name"
      class="w-3/12"
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
      field="projectRef.name"
      header="Project"
      class="w-2/12"
      filter-field="projectId"
      :show-filter-match-modes="false"
      :show-apply-button="false"
    >
      <template #filter="{ filterModel, filterCallback }">
        <AutoComplete
          v-model="filterModel.value"
          dropdown
          :suggestions="projects"
          option-label="name"
          force-selection
          @change="filterCallback()"
          @complete="projectItems($event.query)"
        />
      </template>
    </Column>

    <Column
      field="projectSiteRef.name"
      header="Site"
      class="w-2/12"
      filter-field="projectSiteId"
      :show-filter-match-modes="false"
      :show-apply-button="false"
    >
      <template #filter="{ filterModel, filterCallback }">
        <AutoComplete
          v-model="filterModel.value"
          dropdown
          :suggestions="sites"
          option-label="name"
          force-selection
          @change="filterCallback()"
          @complete="siteItems(filters['projectId'].value?.id ?? null, $event.query)"
        />
      </template>
    </Column>

    <Column
      field="status"
      header="Status"
      class="w-1/12"
      :show-filter-match-modes="false"
      :show-apply-button="false"
    >
      <template #filter="{ filterModel, filterCallback }">
        <Select
          v-model="filterModel.value"
          :options="['NEW', 'ASSIGN', 'DEPLOYED', 'RECOVERED', 'CANCELLED']"
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
          :to="`/deployments/${slotProps.data.id}`"
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
