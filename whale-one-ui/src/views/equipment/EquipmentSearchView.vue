<script setup lang="ts">
import { computed, onMounted, ref, type Ref, toRef } from 'vue'

import Button from 'primevue/button'
import Column from 'primevue/column'
import DataTable from 'primevue/datatable'
import Select from 'primevue/select'

import FilterField from '@/components/FilterField.vue'

import { invokeEquipmentTypeListGet } from '@/client/equipmentTypeClient.ts'
import { invokeAttributeListGet } from '@/client/equipmentTypeAttributeClient.ts'
import { invokeEquipmentSearch, invokeExportDownload } from '@/client/equipmentClient.ts'

import {
  type AttributeFilterModel,
  type AttributeTypeModel,
  FilterOperation,
} from '@/model/AttributeTypeModel.ts'

import { useListViewStore } from '@/stores/listView.ts'

import type {
  EquipmentTypeManufacturerModel,
  EquipmentTypeModel,
} from '@/model/EquipmentTypeModel.ts'
import type EquipmentTypeAttributeModel from '@/model/EquipmentTypeAttributeModel.ts'
import { type PageModel } from '@/model/BaseModel.ts'
import { type EquipmentModel, EquipmentStatus, findAttributeById } from '@/model/EquipmentModel.ts'

const equipmentType: Ref<EquipmentTypeModel | null> = ref(null)
const equipmentTypes: Ref<EquipmentTypeModel[]> = ref([])
const equipmentTypeAttributes: Ref<EquipmentTypeAttributeModel[]> = ref([])

const listViewStore = useListViewStore()
const equipmentSearchViewConfig = toRef(() => listViewStore.state.equipmentSearch)

const list: Ref<PageModel<EquipmentModel> | null> = ref(null)

const filters: Ref<FilterFieldOption[]> = ref([])

const attributeTypes: Ref<AttributeTypeModel[]> = computed(() => {
  const options: AttributeTypeModel[] = [
    { name: 'Name', type: 'text' },
    { name: 'Manufacturer', type: 'select', metadata: { options: [...manufacturers.value] } },
    { name: 'Model', type: 'select', metadata: { options: [...models.value] } },
    { name: 'Status', type: 'select', metadata: { options: [...Object.values(EquipmentStatus)] } },
    { name: 'Active', type: 'text' },
  ]
  equipmentTypeAttributes.value.forEach((attribute) => {
    options.push({ name: attribute.name, type: attribute.type, metadata: attribute.metadata })
  })
  return options
})
const attributeType: Ref<AttributeTypeModel | null> = ref(null)

const manufacturerObjects = computed(() => {
  return (equipmentType.value?.metadata?.manufacturers as EquipmentTypeManufacturerModel[]) || []
})

const manufacturers = computed(() => {
  return manufacturerObjects.value.map((m) => m.name).sort()
})

const models = computed(() => {
  return manufacturerObjects.value.flatMap((m) => m.models).sort()
})

interface FilterFieldOption {
  attribute: AttributeTypeModel
  model: AttributeFilterModel
}

const firstRef = ref(0)
const loading = ref(false)
const exporting = ref(false)

const getEquipmentTypes = () => {
  invokeEquipmentTypeListGet(0, 100, {})
    .then((data) => {
      equipmentTypes.value = data.items
      return data.items
    })
    .catch(() => {})
}

const onEquipmentTypeChange = (newValue: EquipmentTypeModel | null) => {
  if (newValue) {
    invokeAttributeListGet('equipment', newValue.id)
      .then((data) => {
        equipmentTypeAttributes.value = data
        list.value = null
      })
      .catch(() => {})
  } else {
    equipmentTypeAttributes.value = []
  }
  filters.value = []
}

const addFilter = () => {
  if (attributeType.value) {
    filters.value.push({
      attribute: attributeType.value,
      model: {
        field: '',
        value: '',
        operator:
          attributeType.value.type === 'select' ? FilterOperation.IN : FilterOperation.EQUALS,
      },
    })
    attributeType.value = null
  }
}

const loadPage = (first: number, page: number, size: number) => {
  if (!equipmentType.value) {
    return
  }
  loading.value = true
  invokeEquipmentSearch(page, size, prepareFilters(equipmentType.value.id))
    .then((data) => (list.value = data))
    .then(() => (firstRef.value = first))
    .finally(() => (loading.value = false))
}

const downloadExport = () => {
  if (!equipmentType.value) {
    return
  }
  exporting.value = true
  invokeExportDownload(prepareFilters(equipmentType.value.id)).finally(
    () => (exporting.value = false),
  )
}

function prepareFilters(typeId: number) {
  return [
    { field: 'TypeId', value: '' + typeId, operator: FilterOperation.EQUALS },
    ...filters.value.map((filter) => ({
      field: filter.attribute.name,
      value: filter.model.value,
      operator: filter.model.operator,
    })),
  ]
}

onMounted(() => {
  getEquipmentTypes()
})
</script>

<template>
  <div class="mt-5">
    <div class="flex items-center gap-2">
      <h1 class="text-xl font-bold">Equipment Search</h1>
      <Select
        v-model="equipmentType"
        :options="equipmentTypes"
        option-label="name"
        placeholder="Select Equipment Type"
        class="w-72"
        @change="onEquipmentTypeChange($event.value)"
      />
      <Button
        label="Search"
        icon="pi pi-search"
        :disabled="equipmentType === null"
        @click="loadPage(0, 0, equipmentSearchViewConfig.pageSize)"
      />
    </div>

    <div
      v-if="equipmentType"
      class="mt-2 flex flex-col gap-2"
    >
      <template
        v-for="(filter, index) in filters"
        :key="index"
      >
        <FilterField
          v-model="filters[index].model"
          :attribute-type="filters[index].attribute"
          @delete="filters.splice(index, 1)"
        />
      </template>

      <div class="flex items-center gap-2">
        <Select
          :options="attributeTypes"
          v-model="attributeType"
          option-label="name"
          placeholder="Select Field"
          class="w-72"
        />
        <Button
          icon="pi pi-plus"
          severity="secondary"
          @click="addFilter()"
        />
      </div>
    </div>

    <DataTable
      v-if="list"
      class="mt-2"
      :value="list?.items"
      :total-records="list?.totalElements"
      :rows="equipmentSearchViewConfig.pageSize"
      v-model:first="firstRef"
      :loading="loading"
      size="small"
      paginator
      row-hover
      lazy
      @page="loadPage($event.first, $event.page, $event.rows)"
    >
      <Column
        field="id"
        header="Id"
      />

      <Column
        field="name"
        header="Name"
      />

      <Column
        field="manufacturer"
        header="Manufacturer"
      />

      <Column
        field="model"
        header="Model"
      />

      <Column
        field="status"
        header="Status"
      />

      <Column
        field="active"
        header="Active"
      >
        <template #body="slotProps">
          <span
            class="pi"
            :class="slotProps.data.active ? 'pi-check' : 'pi-times'"
          ></span>
        </template>
      </Column>

      <template
        v-for="attribute in equipmentTypeAttributes"
        :key="attribute.id"
      >
        <Column
          :field="attribute.name"
          :header="attribute.name"
        >
          <template #body="slotProps: { data: EquipmentModel }">
            <span v-if="attribute.type !== 'files'">{{
              findAttributeById(slotProps.data, attribute.id)
            }}</span>
          </template>
        </Column>
      </template>

      <template #paginatorstart>
        <div class="flex items-center gap-1">
          <span class="font-semibold">Count: {{ list?.totalElements }}</span>
          <Button
            v-if="list?.items.length"
            icon="pi pi-file-export"
            title="Export"
            size="small"
            variant="text"
            severity="secondary"
            :loading="exporting"
            @click="downloadExport()"
          />
        </div>
      </template>

      <template #paginatorend>
        <Select
          v-model="equipmentSearchViewConfig.pageSize"
          :options="[10, 20, 50, 100, 250]"
          @change="loadPage(0, 0, $event.value)"
        />
      </template>
    </DataTable>
  </div>
</template>
