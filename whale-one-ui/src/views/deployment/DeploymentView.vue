<script setup lang="ts">
import { onMounted, ref, type Ref } from 'vue'

import AutoComplete from 'primevue/autocomplete'
import Button from 'primevue/button'
import FloatLabel from 'primevue/floatlabel'
import Menu from 'primevue/menu'
import Panel from 'primevue/panel'
import Tag from 'primevue/tag'
import Timeline from 'primevue/timeline'

import DeploymentPanel from '@/components/DeploymentPanel.vue'
import EntityHeader from '@/components/EntityHeader.vue'
import {
  invokeDeploymentEquipmentAdd,
  invokeDeploymentEquipmentListGet,
  invokeDeploymentGet,
  invokeDeploymentStatusUpdate,
  invokeDeploymentUpdate,
} from '@/client/deploymentClient.ts'
import { invokeEquipmentTypeItemListGet } from '@/client/equipmentTypeClient.ts'
import { invokeEquipmentItemListGet } from '@/client/equipmentClient.ts'

import type { BaseRefModel } from '@/model/BaseModel.ts'
import type { DeploymentEquipmentItemModel, DeploymentModel } from '@/model/DeploymentModel.ts'
import DeploymentEquipment from '@/components/DeploymentEquipment.vue'
import DeploymentInfo from '@/components/DeploymentInfo.vue'
import { invokeCampaignListGet } from '@/client/projectClient.ts'
import type { ProjectCampaignModel } from '@/model/ProjectModel.ts'
import DeploymentPanelDeploymentInfo from '@/components/DeploymentPanelDeploymentInfo.vue'
import DeploymentPanelRecoveryInfo from '@/components/DeploymentPanelRecoveryInfo.vue'

const props = defineProps<{
  id: number
}>()

const model: Ref<DeploymentModel> = ref({
  id: 0,
  version: 0,
  createdAt: '',
  createdBy: {
    id: 0,
    name: '',
  },
  name: '',
  description: '',
  status: '',
  projectRef: {
    id: 0,
    name: '',
  },
  projectSiteRef: {
    id: 0,
    name: '',
  },
  platform: null,
  providerOrganisations: null,
  providerParticipants: null,
})

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

const equipmentType: Ref<BaseRefModel | null> = ref(null)
const equipmentTypes: Ref<BaseRefModel[]> = ref([])

const equipmentList: Ref<BaseRefModel[]> = ref([])
const equipment: Ref<BaseRefModel | null> = ref(null)

const deploymentEquipmentList: Ref<DeploymentEquipmentItemModel[]> = ref([])

const getDeployment = () => {
  return invokeDeploymentGet(props.id)
    .then((data) => (model.value = data))
    .catch(() => {})
}

const equipmentTypeItems = (q: string | null) => {
  invokeEquipmentTypeItemListGet(q)
    .then((data) => (equipmentTypes.value = data))
    .catch(() => {})
}

const equipmentItems = (typeId: number, q: string | null) => {
  invokeEquipmentItemListGet(typeId, q)
    .then((data) => (equipmentList.value = data))
    .catch(() => {})
}

const getDeploymentEquipment = () => {
  invokeDeploymentEquipmentListGet(props.id)
    .then((data) => (deploymentEquipmentList.value = data))
    .catch(() => {})
}

const addEquipment = () => {
  if (equipment.value === null) {
    return
  }
  invokeDeploymentEquipmentAdd(props.id, equipment.value.id)
    .then(() => getDeploymentEquipment())
    .then(() => {
      equipmentType.value = null
      equipment.value = null
    })
    .catch(() => {})
}

const updateDeployment = (callback: () => void) => {
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
  return statuses[model.value.status as DeploymentStatus] >= statuses[status]
}

const hasStatusOf = (status: DeploymentStatus) => {
  return model.value.status === status
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
  <div class="mt-5">
    <div class="flex flex-col my-4">
      <EntityHeader
        header="Deployment"
        :model="model"
      />

      <Timeline />
      <div class="mt-3 flex gap-1">
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

      <div class="p-timeline p-timeline-vertical">
        <DeploymentPanel
          :start="false"
          icon="pi pi-plus"
          :icon-classes="`mt-5 ${hasStatusOfAtLeast('NEW') ? 'bg-cyan-100 hover:bg-cyan-300' : ''}`"
        >
          <template #default>
            <Panel
              header="Initial Info"
              class="mt-3"
              toggleable
            >
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
          :icon-classes="`${hasStatusOfAtLeast('ASSIGN') ? 'bg-cyan-100 hover:bg-cyan-300' : ''}`"
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
                    <DeploymentEquipment
                      :deployment-equipment="deploymentEquipment"
                      @equipment-deleted="getDeploymentEquipment()"
                    />
                  </template>
                </div>

                <div
                  v-if="hasStatusOf('ASSIGN')"
                  class="mt-3 flex flex-wrap gap-3"
                >
                  <FloatLabel variant="on">
                    <AutoComplete
                      v-model="equipmentType"
                      dropdown
                      :suggestions="equipmentTypes"
                      option-label="name"
                      force-selection
                      @complete="equipmentTypeItems($event.query)"
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
                      @complete="equipmentItems(equipmentType?.id ?? -1, $event.query)"
                    />
                    <label for="1name">Equipment</label>
                  </FloatLabel>
                  <Button
                    label="Add"
                    :disabled="equipment === null"
                    @click="addEquipment()"
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
          :icon-classes="`${hasStatusOfAtLeast('DEPLOYED') ? 'bg-cyan-100 hover:bg-cyan-300' : ''}`"
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
          :icon-classes="`${hasStatusOfAtLeast('RECOVERED') ? 'bg-cyan-100 hover:bg-cyan-300' : ''}`"
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
          :icon-classes="`${hasStatusOfAtLeast('ANALYZED') ? 'bg-cyan-100 hover:bg-cyan-300' : ''}`"
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
    </div>
  </div>
</template>
