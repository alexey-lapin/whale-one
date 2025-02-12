<script setup lang="ts">
import Column from 'primevue/column'
import DataTable from 'primevue/datatable'
import Button from 'primevue/button'
import { onMounted, ref, type Ref } from 'vue'
import type PageModel from '@/model/PageModel.ts'
import type { EquipmentElementModel } from '@/model/EquipmentModel.ts'
import { FilterMatchMode } from '@primevue/core/api'
import Select from 'primevue/select'
import InputText from 'primevue/inputtext'

const list: Ref<PageModel<EquipmentElementModel> | null> = ref(null)

const loading = ref(false)
const pageSize = ref(10)

onMounted(() => {
  loadPage(0, pageSize.value)
  equipmentTypeItems(null)
})

const loadPage = (page: number, size: number) => {
  loading.value = true
  fetch(`/api/equipment?page=${page}&size=${size}&typeId=${filters.value['typeId'].value ?? ''}&name=${filters.value['name'].value ?? ''}`)
    .then(response => response.json())
    .then(data => list.value = data)
    .catch(error => console.error(error))
    .finally(() => loading.value = false)
}

const equipmentTypeItems = (q: string | null) => {
  fetch(`/api/equipment/types/items?q=${q ?? ''}`)
    .then(response => response.json())
    .then(data => equipmentTypes.value = data)
    .catch(error => console.error(error))
}

const filters = ref({
  name: { value: null, matchMode: FilterMatchMode.CONTAINS },
  typeId: { value: null, matchMode: FilterMatchMode.EQUALS }
})

const equipmentTypes = ref([])
</script>

<template>
  <div>
    <div>
      <DataTable :value="list?.items"
                 :total-records="list?.totalElements"
                 :rows="pageSize"
                 :rowsPerPageOptions="[5, 10, 20, 50]"
                 :loading="loading"
                 filterDisplay="menu"
                 v-model:filters="filters"
                 currentPageReportTemplate="{first} to {last} of {totalRecords}"
                 lazy
                 paginator
                 @page="loadPage($event.page, $event.rows)"
                 @filter="loadPage(0, $event.rows)">
        <template #header>
          <div class="flex flex-wrap items-center gap-2">
            <span class="text-xl font-bold flex-grow">Equipment</span>
            <router-link v-slot="{ href, navigate }" to="/equipment/types">
              <a :href="href" @click="navigate">
                <Button label="Setup" icon="pi pi-cog" severity="secondary" />
              </a>
            </router-link>
            <Button icon="pi pi-refresh" severity="secondary" />
            <router-link v-slot="{ href, navigate }" to="/equipment/new">
              <a :href="href" @click="navigate">
                <Button icon="pi pi-plus" severity="primary" />
              </a>
            </router-link>
          </div>
        </template>
        <Column field="id" header="Id" style="width: 10%"></Column>
        <Column field="name"
                header="Name"
                style="width: 40%"
                filterField="name"
                :show-filter-match-modes="false"
                :show-apply-button="false">
          <template #filter="{ filterModel, filterCallback }">
            <InputText v-model="filterModel.value"
                       @change="filterCallback()" />
          </template>
        </Column>
        <Column field="type.name"
                header="Type"
                style="width: 10%"
                filterField="typeId"
                :show-filter-match-modes="false"
                :show-apply-button="false">
          <template #filter="{ filterModel, filterCallback }">
            <Select v-model="filterModel.value"
                    :options="equipmentTypes"
                    option-label="name"
                    option-value="id"
                    @change="filterCallback()" />
          </template>
        </Column>
        <Column style="width: 10%">
          <template #body="slotProps">
            <router-link v-slot="{ href, navigate }" :to="`/equipment/${slotProps.data.id}`">
              <a :href="href" @click="navigate">
                <Button label="Edit" size="small" variant="outlined" severity="secondary" />
              </a>
            </router-link>
          </template>
        </Column>
      </DataTable>
    </div>
  </div>
</template>
