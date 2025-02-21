<script setup lang="ts">
import { onMounted, ref, type Ref } from 'vue'

import Button from 'primevue/button'
import Column from 'primevue/column'
import DataTable from 'primevue/datatable'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import { FilterMatchMode } from '@primevue/core/api'

import type { EquipmentElementModel } from '@/model/EquipmentModel.ts'
import type { BaseRefModel, PageModel } from '@/model/BaseModel.ts'
import { invokeEquipmentListGet } from '@/client/equipmentClient.ts'
import { invokeEquipmentTypeItemListGet } from '@/client/equipmentTypeClient.ts'

const list: Ref<PageModel<EquipmentElementModel> | null> = ref(null)

const loading = ref(false)
const pageSize = ref(10)

const loadPage = (page: number, size: number) => {
  loading.value = true
  invokeEquipmentListGet(page, size, filters.value['typeId'].value, filters.value['name'].value)
    .then((data) => (list.value = data))
    .catch(() => {})
    .finally(() => (loading.value = false))
}

const equipmentTypeItems = (q: string | null) => {
  invokeEquipmentTypeItemListGet(q)
    .then((data) => (equipmentTypes.value = data))
    .catch(() => {})
}

const filters = ref({
  name: { value: null, matchMode: FilterMatchMode.CONTAINS },
  typeId: { value: null, matchMode: FilterMatchMode.EQUALS },
})

onMounted(() => {
  loadPage(0, pageSize.value)
  equipmentTypeItems(null)
})

const equipmentTypes: Ref<BaseRefModel[]> = ref([])
</script>

<template>
  <div>
    <div>
      <DataTable
        :value="list?.items"
        :total-records="list?.totalElements"
        v-model:rows="pageSize"
        :loading="loading"
        filter-display="menu"
        v-model:filters="filters"
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
            <span class="text-xl font-bold flex-grow">Equipment</span>
            <router-link
              v-slot="{ href, navigate }"
              to="/equipment/types"
            >
              <a
                :href="href"
                @click="navigate"
              >
                <Button
                  label="Setup"
                  icon="pi pi-cog"
                  severity="secondary"
                />
              </a>
            </router-link>
            <Button
              icon="pi pi-refresh"
              severity="secondary"
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
        >
        </Column>
        <Column
          field="name"
          header="Name"
          class="w-5/12"
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
          class="w-4/12"
          filterField="typeId"
          :show-filter-match-modes="false"
          :show-apply-button="false"
        >
          <template #filter="{ filterModel, filterCallback }">
            <Select
              v-model="filterModel.value"
              :options="equipmentTypes"
              option-label="name"
              option-value="id"
              @change="filterCallback()"
            />
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
          class="w-1/12 !text-end"
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
        <!--        <template #paginatorstart></template>-->
        <!--        <template #paginatorend>-->
        <!--          <div class="flex gap-2 items-center">-->
        <!--            <span>Total: {{ list?.totalElements }}</span>-->
        <!--            <Select-->
        <!--              v-model="pageSize"-->
        <!--              :options="[1, 5, 10, 20, 50]"-->
        <!--              @change="loadPage(0, $event.value)"-->
        <!--            />-->
        <!--          </div>-->
        <!--        </template>-->
        <!--        <template #paginatorcontainer="slotProps: any">-->
        <!--          <Paginator-->
        <!--            :first="slotProps.first"-->
        <!--            :last="slotProps.last"-->
        <!--            :rows="slotProps.rows"-->
        <!--            :page="slotProps.page"-->
        <!--            :pageCount="slotProps.pageCount"-->
        <!--            :totalRecords="slotProps.totalRecords"-->
        <!--            :firstPageCallback="slotProps.firstPageCallback"-->
        <!--            :lastPageCallback="slotProps.lastPageCallback"-->
        <!--            :prevPageCallback="slotProps.prevPageCallback"-->
        <!--            :nextPageCallback="slotProps.nextPageCallback"-->
        <!--            :rowChangeCallback="slotProps.rowChangeCallback"-->
        <!--          />-->
        <!--        </template>-->
      </DataTable>
    </div>
  </div>
</template>
