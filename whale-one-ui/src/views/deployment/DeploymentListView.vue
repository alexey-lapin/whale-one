<script setup lang="ts">
import Button from 'primevue/button'
import Column from 'primevue/column'
import DataTable from 'primevue/datatable'
import Select from 'primevue/select'
import InputText from 'primevue/inputtext'
import { onMounted, ref, type Ref } from 'vue'
import type { PageModel } from '@/model/BaseModel.ts'
import type { EquipmentElementModel } from '@/model/EquipmentModel.ts'
import type { DeploymentModel } from '@/model/DeploymentModel.ts'
import { invokeEquipmentListGet } from '@/client/equipmentClient.ts'
import { invokeDeploymentListGet } from '@/client/deploymentClient.ts'

const list: Ref<PageModel<DeploymentModel> | null> = ref(null)

const loading = ref(false)
const pageSize = ref(10)

const loadPage = (page: number, size: number) => {
  loading.value = true
  invokeDeploymentListGet(page, size)
    .then((data) => (list.value = data))
    .catch(() => {})
    .finally(() => (loading.value = false))
}

onMounted(() => {
  loadPage(0, pageSize.value)
})
</script>

<template>
  <DataTable
    :value="list?.items"
    :total-records="list?.totalElements"
    v-model:rows="pageSize"
    :loading="loading"
    size="small"
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
        <span class="text-xl font-bold flex-grow">Deployments</span>
        <Button
          icon="pi pi-refresh"
          severity="secondary"
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
      field="id"
      header="Id"
      class="w-1/12"
    >
    </Column>

    <Column
      field="name"
      header="Name"
      class="w-3/12"
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
      field="projectRef.name"
      header="Project"
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
      field="projectSiteRef.name"
      header="Site"
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
      field="status"
      header="Status"
      class="w-1/12"
    >
<!--      <template #body="slotProps">-->
<!--            <span-->
<!--              v-if="slotProps.data.active"-->
<!--              class="pi pi-check"-->
<!--            ></span>-->
<!--        <span-->
<!--          v-else-->
<!--          class="pi pi-times"-->
<!--        ></span>-->
<!--      </template>-->
    </Column>

    <Column
      header="Actions"
      class="w-1/12 !text-end"
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

<style scoped>
:deep(.p-datatable-header-cell:hover) {
  background-color: var(--p-surface-50);
}
</style>
