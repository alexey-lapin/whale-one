<script setup lang="ts">
import { computed, onMounted, ref, type Ref, toRef } from 'vue'
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

import { useAuthStore } from '@/stores/auth.ts'
import { useConfirm } from 'primevue/useconfirm'
import { deleteConfirm } from '@/utils/confirms.ts'

import {
  invokeEquipmentDelete,
  invokeEquipmentListGet,
  invokeEquipmentToggleActive,
} from '@/client/equipmentClient.ts'
import { invokeEquipmentTypeListGet } from '@/client/equipmentTypeClient.ts'
import { useListViewStore } from '@/stores/listView.ts'

import { type EquipmentElementModel, EquipmentStatus } from '@/model/EquipmentModel.ts'
import type { BaseRefModel, FilterConditions, PageModel } from '@/model/BaseModel.ts'
import type {
  EquipmentTypeManufacturerModel,
  EquipmentTypeModel,
} from '@/model/EquipmentTypeModel.ts'

const auth = useAuthStore()
const confirm = useConfirm()

const listViewStore = useListViewStore()
const equipmentTypeListViewConfig = toRef(() => listViewStore.state.equipmentTypes)
const equipmentListViewConfig = toRef(() => listViewStore.state.equipment)

const list: Ref<PageModel<EquipmentElementModel> | null> = ref(null)
const firstRef = ref(0)

const equipmentTypeItems: Ref<BaseRefModel[]> = ref([])
const equipmentTypes: Ref<EquipmentTypeModel[]> = ref([])

const loading = ref(false)

const loadPage = (first: number, page: number, size: number) => {
  loading.value = true
  invokeEquipmentListGet(page, size, filters.value)
    .then((data) => (list.value = data))
    .then(() => (firstRef.value = first))
    .catch(() => {})
    .finally(() => (loading.value = false))
}

const reload = () => {
  loadPage(0, 0, equipmentListViewConfig.value.pageSize)
}

const getEquipmentTypes = () => {
  invokeEquipmentTypeListGet(0, 100, {})
    .then((data) => {
      equipmentTypes.value = data.items
      return data.items
    })
    .then((data) => (equipmentTypeItems.value = data.map((et) => ({ id: et.id, name: et.name }))))
    .catch(() => {})
}

const filters: Ref<FilterConditions> = ref({})

const initFilters = () => {
  filters.value = {
    name: { value: null, matchMode: FilterMatchMode.CONTAINS },
    typeId: { value: [], matchMode: FilterMatchMode.IN },
    manufacturer: { value: null, matchMode: FilterMatchMode.CONTAINS },
    model: { value: null, matchMode: FilterMatchMode.CONTAINS },
    active: {
      value: equipmentListViewConfig.value.showActiveOnly === true ? true : null,
      matchMode: FilterMatchMode.EQUALS,
    },
    status: { value: [], matchMode: FilterMatchMode.IN },
  }
}

initFilters()

const resetFilters = () => {
  initFilters()
  firstRef.value = 0
  reload()
}

const debouncedFilterCallback = useDebounceFn((callback) => callback(), 500)

const manufacturers = computed(() => {
  return equipmentTypes.value
    .filter((et) => !et.isAssembly)
    .filter(
      (et) =>
        filters.value.typeId.value === null ||
        (filters.value.typeId.value as number[]).length == 0 ||
        (filters.value.typeId.value as number[]).includes(et.id),
    )
    .flatMap((et) => (et.metadata?.manufacturers as EquipmentTypeManufacturerModel[]) || [])
})

const models = computed(() => {
  return manufacturers.value
    .filter(
      (m) =>
        filters.value.manufacturer.value === null || m.name === filters.value.manufacturer.value,
    )
    .flatMap((m) => m.models)
})

const settingsPopover = ref()

const toggleSettingsPopover = (event: Event) => {
  settingsPopover.value.toggle(event)
}

const isIdVisible = ref(false)

const isTypeFilterActive = (id: number) => {
  const types = filters.value.typeId.value as number[] | null
  if (types) {
    return types.includes(id)
  }
  return false
}

const onTypeFilterClick = (id: number) => {
  const types = filters.value.typeId.value as number[] | null
  if (types) {
    if (types.includes(id)) {
      ;(filters.value.typeId.value as number[]).splice(types.indexOf(id), 1)
    } else {
      ;(filters.value.typeId.value as number[]).push(id)
    }
  }
  reload()
}

const toggleActive = (equipment: EquipmentElementModel) => {
  invokeEquipmentToggleActive(equipment.id)
    .then(() => {
      equipment.active = !equipment.active
    })
    .catch(() => {})
}

const confirmDelete = (id: number, name: string) => {
  confirm.require(
    deleteConfirm(`Delete Equipment #${id} ${name}?`, () =>
      invokeEquipmentDelete(id)
        .then(() => {
          loadPage(firstRef.value, 0, equipmentListViewConfig.value.pageSize)
        })
        .catch(() => {}),
    ),
  )
}

onMounted(() => {
  reload()
  getEquipmentTypes()
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
      <div class="flex items-center gap-2">
        <Checkbox
          v-model="equipmentListViewConfig.showActiveOnly"
          binary
          @change="resetFilters()"
        />
        <label>Show Active only</label>
      </div>
    </div>
  </Popover>

  <DataTable
    :value="list?.items"
    :total-records="list?.totalElements"
    v-model:rows="equipmentListViewConfig.pageSize"
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
      <div class="flex items-center gap-2">
        <div class="flex-grow flex items-center gap-1">
          <span class="text-xl font-bold">Equipment</span>
          <div class="flex flex-wrap gap-1">
            <template
              v-for="type in equipmentTypeListViewConfig.favorites"
              :key="type.id"
            >
              <EquipmentTypeTag
                :name="type.name"
                :active="isTypeFilterActive(type.id)"
                class="cursor-pointer text-sm"
                @click="onTypeFilterClick(type.id)"
              />
            </template>
          </div>
        </div>
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
      field="type.name"
      header="Type"
      class="w-2/12"
      filterField="typeId"
      :show-filter-match-modes="false"
      :show-apply-button="false"
    >
      <template #filter="{ filterModel, filterCallback }">
        <template
          v-for="item in equipmentTypeItems"
          :key="item.id"
        >
          <div class="flex items-center gap-2">
            <Checkbox
              v-model="filterModel.value"
              :value="item.id"
              @change="filterCallback()"
            />
            <EquipmentTypeTag :name="item.name" />
          </div>
        </template>
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
          option-label="name"
          option-value="name"
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
              :title="`${slotProps.data.assemblyId}`"
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
      header="Deployment"
      class="w-1/12"
    >
      <template #body="slotProps">
        <router-link
          v-if="slotProps.data.deployment"
          v-slot="{ href, navigate }"
          :to="`/deployments/${slotProps.data.deployment.id}`"
        >
          <a
            :href="href"
            @click="navigate"
          >
            <Button
              icon="pi pi-external-link"
              icon-pos="right"
              :label="`${slotProps.data.deployment.name}`"
              size="small"
              variant="outlined"
              severity="secondary"
            />
          </a>
        </router-link>
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
        <template
          v-for="item in Object.values(EquipmentStatus)"
          :key="item"
        >
          <div class="flex items-center gap-2">
            <Checkbox
              v-model="filterModel.value"
              :value="item"
              @change="filterCallback()"
            />
            <span>{{ item }}</span>
          </div>
        </template>
      </template>
    </Column>

    <Column
      v-if="!equipmentListViewConfig.showActiveOnly"
      field="active"
      header="Active"
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
        <Button
          :icon="slotProps.data.active ? 'pi pi-check' : 'pi pi-times'"
          size="small"
          variant="outlined"
          severity="secondary"
          @click="toggleActive(slotProps.data)"
        />
      </template>
    </Column>

    <Column
      header="Actions"
      class="w-1/12"
    >
      <template #body="slotProps">
        <div class="flex gap-1">
          <router-link
            v-slot="{ href, navigate }"
            :to="`/equipment/${slotProps.data.id}`"
          >
            <a
              :href="href"
              @click="navigate"
            >
              <Button
                icon="pi pi-pencil"
                size="small"
                variant="outlined"
                severity="secondary"
              />
            </a>
          </router-link>
          <Button
            v-if="auth.hasAuthority('ADMIN')"
            icon="pi pi-trash"
            size="small"
            variant="outlined"
            class="hover:!text-red-600"
            severity="secondary"
            @click="confirmDelete(slotProps.data.id, slotProps.data.name)"
          />
        </div>
      </template>
    </Column>

    <template #paginatorstart>
      <span class="font-semibold">Count: {{ list?.totalElements }}</span>
    </template>

    <template #paginatorend>
      <Select
        v-model="equipmentListViewConfig.pageSize"
        :options="[10, 20, 50, 100]"
        @change="loadPage(0, 0, $event.value)"
      />
    </template>
  </DataTable>
</template>
