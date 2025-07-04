<script setup lang="ts">
import { onMounted, ref, type Ref } from 'vue'

import AutoComplete from 'primevue/autocomplete'
import Button from 'primevue/button'
import FloatLabel from 'primevue/floatlabel'
import Menu from 'primevue/menu'
import Dialog from 'primevue/dialog'
import Panel from 'primevue/panel'
import Timeline from 'primevue/timeline'
import Tabs from 'primevue/tabs'
import Tab from 'primevue/tab'
import TabList from 'primevue/tablist'
import TabPanel from 'primevue/tabpanel'
import TabPanels from 'primevue/tabpanels'
import Tag from 'primevue/tag'

import {
  invokeDeploymentEquipmentAdd,
  invokeDeploymentEquipmentElementsGet,
  invokeDeploymentGet,
  invokeDeploymentStatusUpdate,
  invokeDeploymentUpdate,
} from '@/client/deploymentClient.ts'
import { invokeEquipmentTypeItemListGet } from '@/client/equipmentTypeClient.ts'
import { invokeEquipmentItemListGet } from '@/client/equipmentClient.ts'
import { invokeCampaignListGet } from '@/client/projectClient.ts'

import DeploymentPanel from '@/components/DeploymentPanel.vue'
import DeploymentInfo from '@/components/DeploymentInfo.vue'
import DeploymentPanelDeploymentInfo from '@/components/DeploymentPanelDeploymentInfo.vue'
import DeploymentPanelRecoveryInfo from '@/components/DeploymentPanelRecoveryInfo.vue'
import EntityHeaderDialog from '@/components/EntityHeaderDialog.vue'

import type { BaseRefModel } from '@/model/BaseModel.ts'
import type { DeploymentEquipmentElementModel, DeploymentModel } from '@/model/DeploymentModel.ts'
import type { ProjectCampaignModel } from '@/model/ProjectModel.ts'
import DeploymentEquipmentElement from '@/components/DeploymentEquipmentElement.vue'
import EquipmentAssembly from '@/components/EquipmentAssembly.vue'
import type { EquipmentTypeItemModel } from '@/model/EquipmentTypeModel.ts'

const props = defineProps<{
  id: number
}>()

const model: Ref<DeploymentModel | null> = ref(null)

const statuses = {
  NEW: 0,
  ASSIGN: 1,
  DEPLOYED: 2,
  RECOVERED: 3,
  ANALYZED: 4,
}

const editing = ref(false)
const editingDeploymentInfo = ref(false)
const editingRecoveryInfo = ref(false)

const campaigns: Ref<ProjectCampaignModel[]> = ref([])

const equipmentTypeItem: Ref<EquipmentTypeItemModel | null> = ref(null)
const equipmentTypeItems: Ref<EquipmentTypeItemModel[]> = ref([])

const equipmentList: Ref<BaseRefModel[]> = ref([])
const equipment: Ref<BaseRefModel | null> = ref(null)

const assembling = ref(false)

const deploymentEquipmentList: Ref<DeploymentEquipmentElementModel[]> = ref([])

const getDeployment = () => {
  return invokeDeploymentGet(props.id)
    .then((data) => (model.value = data))
    .catch(() => {})
}

const getEquipmentTypeItems = (q: string | null) => {
  invokeEquipmentTypeItemListGet(q)
    .then((data) => data.filter(item => item.isDeployable))
    .then((data) => (equipmentTypeItems.value = data))
    .catch(() => {})
}

const equipmentItems = (typeId: number, q: string | null) => {
  invokeEquipmentItemListGet(typeId, q)
    .then((data) => (equipmentList.value = data))
    .catch(() => {})
}

const getDeploymentEquipment = () => {
  invokeDeploymentEquipmentElementsGet(props.id)
    .then((data) => (deploymentEquipmentList.value = data))
    .catch(() => {})
}

const addEquipment = () => {
  if (equipment.value === null) {
    return Promise.resolve()
  }
  return invokeDeploymentEquipmentAdd(props.id, equipment.value.id)
    .then(() => getDeploymentEquipment())
    .then(() => {
      equipmentTypeItem.value = null
      equipment.value = null
    })
    .catch(() => {})
}

const onAssembleCreated = (equipmentId: number) => {
  equipment.value = { id: equipmentId, name: '' }
  addEquipment().then(() => (assembling.value = false))
}

const updateDeployment = (callback: () => void) => {
  if (model.value === null) {
    return
  }
  invokeDeploymentUpdate(model.value)
    .then((data) => (model.value = data))
    .then(() => callback())
    .catch(() => {})
}

const updateStatus = (status: DeploymentStatus) => {
  invokeDeploymentStatusUpdate(props.id, status)
    .then((data) => (model.value = data))
    .catch(() => {})
}

const getCampaigns = () => {
  invokeCampaignListGet(props.id)
    .then((data) => (campaigns.value = data))
    .catch(() => {})
}

type DeploymentStatus = keyof typeof statuses

const hasStatusOfAtLeast = (status: DeploymentStatus) => {
  return (
    model.value !== null && statuses[model.value.status as DeploymentStatus] >= statuses[status]
  )
}

const hasStatusOf = (status: DeploymentStatus) => {
  return model.value?.status === status
}

const menu = ref()
const items = ref([
  {
    label: 'Go to Status',
    items: [
      {
        label: 'NEW',
        command: () => updateStatus('NEW'),
      },
      {
        label: 'ASSIGN',
        command: () => updateStatus('ASSIGN'),
      },
      {
        label: 'DEPLOYED',
        command: () => updateStatus('DEPLOYED'),
      },
      {
        label: 'RECOVERED',
        command: () => updateStatus('RECOVERED'),
      },
      {
        label: 'ANALYZED',
        command: () => updateStatus('ANALYZED'),
      },
    ],
  },
])

const toggle = (event: Event) => {
  menu.value.toggle(event)
}

onMounted(() => {
  getDeployment()
  getDeploymentEquipment()
})
</script>

<template>
  <div
    v-if="model"
    class="my-4 flex flex-col"
  >
    <Timeline />
    <div class="flex gap-1">
      <Tag
        :value="model.status"
        severity="primary"
      />
      <Button
        icon="pi pi-cog"
        size="small"
        severity="secondary"
        text
        @click="toggle"
      />
      <Menu
        ref="menu"
        :model="items"
        :popup="true"
      />
    </div>

    <Tabs value="0">
      <TabList>
        <Tab value="0">Overview</Tab>
        <Tab value="1">Equipment</Tab>
        <Tab value="2">Analysis</Tab>
      </TabList>
      <TabPanels>
        <TabPanel value="0">
          <div class="p-timeline p-timeline-vertical">
            <DeploymentPanel
              :start="false"
              icon="pi pi-plus"
              :highlighted="hasStatusOfAtLeast('NEW')"
            >
              <template #default>
                <Panel
                  header="Initial Info"
                  class="mt-3"
                  toggleable
                >
                  <template #header>
                    <EntityHeaderDialog
                      :model="model"
                      v-slot="{ toggle }"
                    >
                      <span
                        class="p-panel-title cursor-pointer"
                        @click="toggle()"
                      >
                        Overview
                      </span>
                    </EntityHeaderDialog>
                  </template>
                  <template #icons>
                    <Button
                      icon="pi pi-pencil"
                      severity="secondary"
                      variant="text"
                      @click="editing = !editing"
                    />
                  </template>
                  <template #default>
                    <div class="w-full">
                      <DeploymentInfo
                        :editing="editing"
                        v-model="model"
                        @save-clicked="updateDeployment(() => (editing = false))"
                      />
                    </div>
                  </template>
                </Panel>
                <Button
                  v-if="hasStatusOf('NEW')"
                  class="mt-3"
                  label="Assign"
                  @click="updateStatus('ASSIGN')"
                />
              </template>
            </DeploymentPanel>

            <DeploymentPanel
              icon="pi pi-wrench"
              :highlighted="hasStatusOfAtLeast('ASSIGN')"
            >
              <template #default>
                <Panel
                  header="Equipment"
                  class="mt-3"
                  toggleable
                >
                  <template #default>
                    <div class="flex flex-col gap-2">
                      <template v-for="deploymentEquipment in deploymentEquipmentList">
                        <!--                        <DeploymentEquipment-->
                        <!--                          :deployment-equipment="deploymentEquipment"-->
                        <!--                          @equipment-deleted="getDeploymentEquipment()"-->
                        <!--                        />-->
                        <div>
                          <DeploymentEquipmentElement
                            :deployment-id="props.id"
                            :deployment-equipment="deploymentEquipment"
                            @equipment-deleted="getDeploymentEquipment()"
                          />
                        </div>
                      </template>
                    </div>

                    <div
                      v-if="hasStatusOf('ASSIGN')"
                      class="mt-3 flex flex-wrap gap-3"
                    >
                      <FloatLabel variant="on">
                        <AutoComplete
                          v-model="equipmentTypeItem"
                          dropdown
                          :suggestions="equipmentTypeItems"
                          option-label="name"
                          force-selection
                          @complete="getEquipmentTypeItems($event.query)"
                          @change="equipment = null"
                        />
                        <label for="1name">Type</label>
                      </FloatLabel>
                      <FloatLabel variant="on">
                        <AutoComplete
                          v-model="equipment"
                          dropdown
                          :suggestions="equipmentList"
                          option-label="name"
                          force-selection
                          @complete="equipmentItems(equipmentTypeItem?.id ?? -1, $event.query)"
                        />
                        <label for="1name">Equipment</label>
                      </FloatLabel>
                      <Button
                        label="Add"
                        :disabled="equipment === null"
                        @click="addEquipment()"
                      />
                      <Button
                        v-if="equipmentTypeItem?.isAssembly"
                        label="Assemble"
                        severity="secondary"
                        :disabled="equipmentTypeItem === null || equipment !== null"
                        @click="assembling = true"
                      />
                    </div>
                  </template>
                </Panel>
                <Button
                  v-if="hasStatusOf('ASSIGN')"
                  class="mt-3"
                  label="Deploy"
                  @click="updateStatus('DEPLOYED')"
                />
              </template>
            </DeploymentPanel>

            <DeploymentPanel
              icon="pi pi-download"
              :highlighted="hasStatusOfAtLeast('DEPLOYED')"
            >
              <template #default>
                <Panel
                  header="Deployment Info"
                  class="mt-3"
                  toggleable
                >
                  <template #icons>
                    <Button
                      icon="pi pi-pencil"
                      severity="secondary"
                      variant="text"
                      @click="editingDeploymentInfo = !editingDeploymentInfo"
                    />
                  </template>
                  <template #default>
                    <div class="w-full">
                      <DeploymentPanelDeploymentInfo
                        v-model="model"
                        :editing="editingDeploymentInfo"
                        @save-clicked="updateDeployment(() => (editingDeploymentInfo = false))"
                      />
                    </div>
                  </template>
                </Panel>
                <Button
                  v-if="hasStatusOf('DEPLOYED')"
                  class="mt-3"
                  label="Recover"
                  @click="updateStatus('RECOVERED')"
                />
              </template>
            </DeploymentPanel>

            <DeploymentPanel
              icon="pi pi-upload"
              :highlighted="hasStatusOfAtLeast('RECOVERED')"
            >
              <template #default>
                <Panel
                  header="Recovery Info"
                  class="mt-3"
                  toggleable
                >
                  <template #icons>
                    <Button
                      icon="pi pi-pencil"
                      severity="secondary"
                      variant="text"
                      @click="editingRecoveryInfo = !editingRecoveryInfo"
                    />
                  </template>
                  <template #default>
                    <DeploymentPanelRecoveryInfo
                      v-model="model"
                      :editing="editingRecoveryInfo"
                      @save-clicked="updateDeployment(() => (editingRecoveryInfo = false))"
                    />
                  </template>
                </Panel>
                <Button
                  v-if="hasStatusOf('RECOVERED')"
                  class="mt-3"
                  label="Analyze"
                  @click="updateStatus('ANALYZED')"
                />
              </template>
            </DeploymentPanel>

            <DeploymentPanel
              :end="false"
              icon="pi pi-wave-pulse"
              :highlighted="hasStatusOfAtLeast('ANALYZED')"
            >
              <template #default>
                <Panel
                  header="Analysis"
                  class="mt-3"
                  toggleable
                >
                  <template #default>analysis</template>
                </Panel>
              </template>
            </DeploymentPanel>
          </div>
        </TabPanel>
        <TabPanel value="1"></TabPanel>
        <TabPanel value="2"></TabPanel>
      </TabPanels>
    </Tabs>
  </div>

  <Dialog
    v-model:visible="assembling"
    modal
    header="Assembly"
    class="w-96"
    :dismissable-mask="true"
    @hide="assembling = false"
  >
    <EquipmentAssembly
      v-if="equipmentTypeItem"
      :type="equipmentTypeItem"
      @assembled="onAssembleCreated($event)"
    />
  </Dialog>
</template>
