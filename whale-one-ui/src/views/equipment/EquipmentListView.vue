<script setup lang="ts">
import { computed, onMounted, ref, type Ref, watch } from 'vue'
import { useDebounceFn } from '@vueuse/core'

import Button from 'primevue/button'
import Checkbox from 'primevue/checkbox'
import Column from 'primevue/column'
import DataTable from 'primevue/datatable'
import InputText from 'primevue/inputtext'
import Popover from 'primevue/popover'
import Select from 'primevue/select'
import { FilterMatchMode } from '@primevue/core/api'

import EquipmentTypeTag from '@/components/EquipmentTypeTag.vue'

import { invokeEquipmentListGet } from '@/client/equipmentClient.ts'
import {
  invokeEquipmentTypeGet,
  invokeEquipmentTypeItemListGet,
} from '@/client/equipmentTypeClient.ts'

import type { EquipmentElementModel } from '@/model/EquipmentModel.ts'
import type { BaseRefModel, FilterConditions, PageModel } from '@/model/BaseModel.ts'
import type {
  EquipmentTypeManufacturerModel,
  EquipmentTypeModel,
} from '@/model/EquipmentTypeModel.ts'
import { useListViewStore } from '@/stores/listView.ts'

const listViewStore = useListViewStore()

const list: Ref<PageModel<EquipmentElementModel> | null> = ref(null)

const equipmentTypeItems: Ref<BaseRefModel[]> = ref([])
const equipmentType: Ref<EquipmentTypeModel | null> = ref(null)

const loading = ref(false)
// const pageSize = ref(listViewStore.state.equipment.pageSize)
const firstRef = ref(0)

const loadPage = (first: number, page: number, size: number) => {
  loading.value = true
  invokeEquipmentListGet(page, size, filters.value)
    .then((data) => (list.value = data))
    .then(() => (firstRef.value = first))
    .catch(() => {})
    .finally(() => (loading.value = false))
}

const getEquipmentTypeItems = (q: string | null) => {
  invokeEquipmentTypeItemListGet(q)
    .then((data) => (equipmentTypeItems.value = data))
    .catch(() => {})
}

const getEquipmentType = (id: number) => {
  return invokeEquipmentTypeGet(id)
    .then((data) => (equipmentType.value = data))
    .catch(() => {})
}

const filters: Ref<FilterConditions> = ref({})

const initFilters = () => {
  filters.value = {
    name: { value: null, matchMode: FilterMatchMode.CONTAINS },
    typeId: { value: null, matchMode: FilterMatchMode.EQUALS },
    manufacturer: { value: null, matchMode: FilterMatchMode.CONTAINS },
    model: { value: null, matchMode: FilterMatchMode.CONTAINS },
  }
}

initFilters()

const resetFilters = () => {
  initFilters()
  firstRef.value = 0
  loadPage(0, 0, listViewStore.state.equipment.pageSize)
}

const debouncedFilterCallback = useDebounceFn((callback) => callback(), 500)

watch(
  () => filters.value.typeId,
  (newValue) => {
    if (newValue.value) {
      getEquipmentType(newValue.value)
    } else {
      equipmentType.value = null
    }
  },
)

const manufacturers = computed(() => {
  return (
    (equipmentType.value?.metadata?.manufacturers as EquipmentTypeManufacturerModel[]) || []
  ).map((m) => m.name)
})

const models = computed(() => {
  return (
    ((equipmentType.value?.metadata?.manufacturers as EquipmentTypeManufacturerModel[]) || []).find(
      (m) => m.name === filters.value.manufacturer.value,
    )?.models || []
  )
})

const settingsPopover = ref()

const toggleSettingsPopover = (event: Event) => {
  settingsPopover.value.toggle(event)
}

const isIdVisible = ref(false)

onMounted(() => {
  loadPage(0, 0, listViewStore.state.equipment.pageSize)
  getEquipmentTypeItems(null)
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
    v-model:rows="listViewStore.state.equipment.pageSize"
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
        <span class="text-xl font-bold flex-grow">Equipment</span>
        <Button
          title="Reset Filters"
          icon="pi pi-filter-slash"
          severity="secondary"
          @click="resetFilters()"
        />
        <Button
          title="Settings"
          icon="pi pi-cog"
          severity="secondary"
          @click="toggleSettingsPopover"
        />
        <router-link
          v-slot="{ href, navigate }"
          to="/equipment/new"
        >
          <a
            :href="href"
            @click="navigate"
          >
            <Button
              title="Create New"
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
      filterField="name"
      :show-filter-match-modes="false"
      :show1-apply-button="false"
    >
      <template #filter="{ filterModel, filterCallback }">
        <InputText
          v-model="filterModel.value"
          @change1="filterCallback()"
        />
      </template>
    </Column>

    <Column
      field="type.name"
      header="Type"
      class="w-2/12"
      filterField="typeId"
      :show-filter-match-modes="false"
      :show-apply-button="false"
    >
      <template #filter="{ filterModel, filterCallback }">
        <Select
          v-model="filterModel.value"
          :options="equipmentTypeItems"
          option-label="name"
          option-value="id"
          @change="filterCallback()"
        />
      </template>
      <template #body="slotProps">
        <EquipmentTypeTag :name="slotProps.data.type.name" />
      </template>
    </Column>

    <Column
      field="manufacturer"
      header="Manufacturer"
      class="w-2/12"
      :show-filter-match-modes="false"
      :show-apply-button="false"
    >
      <template #filter="{ filterModel, filterCallback }">
        <Select
          v-model="filterModel.value"
          :options="manufacturers"
          editable
          @change="debouncedFilterCallback(filterCallback)"
        />
      </template>
    </Column>

    <Column
      field="model"
      header="Model"
      class="w-2/12"
      :show-filter-match-modes="false"
      :show-apply-button="false"
    >
      <template #filter="{ filterModel, filterCallback }">
        <Select
          v-model="filterModel.value"
          :options="models"
          editable
          @change="debouncedFilterCallback(filterCallback)"
        />
      </template>
    </Column>

    <Column
      field="assemblyId"
      header="Assembly"
      class="w-1/12"
    >
      <template #body="slotProps">
        <router-link
          v-if="slotProps.data.assemblyId"
          v-slot="{ href, navigate }"
          :to="`/equipment/${slotProps.data.assemblyId}`"
        >
          <a
            :href="href"
            @click="navigate"
          >
            <Button
              icon="pi pi-external-link"
              icon-pos="right"
              :label="`${slotProps.data.assemblyId}`"
              size="small"
              variant="outlined"
              severity="secondary"
            />
          </a>
        </router-link>
      </template>
    </Column>

    <Column
      field="deploymentId"
      header="Deployed"
      class="w-1/12"
    >
      <template #body="slotProps">
        <router-link
          v-if="slotProps.data.deploymentId"
          v-slot="{ href, navigate }"
          :to="`/deployments/${slotProps.data.deploymentId}`"
        >
          <a
            :href="href"
            @click="navigate"
          >
            <Button
              icon="pi pi-external-link"
              icon-pos="right"
              :label="`${slotProps.data.deploymentId}`"
              size="small"
              variant="outlined"
              severity="secondary"
            />
          </a>
        </router-link>
      </template>
    </Column>

    <Column
      field="active"
      header="Active"
      class="w-1/12"
    >
      <template #body="slotProps">
        <span
          v-if="slotProps.data.active"
          class="pi pi-check"
        ></span>
        <span
          v-else
          class="pi pi-times"
        ></span>
      </template>
    </Column>

    <Column
      header="Actions"
      class="w-1/12"
    >
      <template #body="slotProps">
        <router-link
          v-slot="{ href, navigate }"
          :to="`/equipment/${slotProps.data.id}`"
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
    <template #paginatorstart></template>
    <template #paginatorend>
      <div class="flex items-center gap-2">
        <span>count: {{ list?.totalElements }}</span>
        <Select
          v-model="listViewStore.state.equipment.pageSize"
          :options="[10, 20, 50, 100]"
          @change="loadPage(0, 0, $event.value)"
        />
      </div>
    </template>
  </DataTable>
</template>
