<script setup lang="ts">
import Button from 'primevue/button'
import Menubar from 'primevue/menubar'

import type { MenuItem } from 'primevue/menuitem'
import { useAuthStore } from '@/stores/auth.ts'

let auth = useAuthStore()

const items: MenuItem[] = [
  {
    label: 'Projects',
    route: '/projects'
  },
  {
    label: 'Deployments',
    route: '/deployments'
  },
  {
    label: 'Equipment',
    route: '/equipment'
  }
]
</script>

<template>
  <Menubar :model="items">
    <template #start>
      <h1 class="text-3xl font-semibold">🐳 Whale One</h1>
    </template>
    <!--    <template #item="{ item, props, hasSubmenu, root }">-->
    <!--      <a v-ripple class="flex items-center" v-bind="props.action">-->
    <!--        <span>{{ item.label }}</span>-->
    <!--        <Badge v-if="item.badge" :class="{ 'ml-auto': !root, 'ml-2': root }" :value="item.badge" />-->
    <!--        <span v-if="item.shortcut" class="ml-auto border border-surface rounded bg-emphasis text-muted-color text-xs p-1">{{ item.shortcut }}</span>-->
    <!--        <i v-if="hasSubmenu" :class="['pi pi-angle-down ml-auto', { 'pi-angle-down': root, 'pi-angle-right': !root }]"></i>-->
    <!--      </a>-->
    <!--    </template>-->
    <template #item="{ item, props, hasSubmenu }">
      <router-link v-if="item.route" v-slot="{ href, navigate, isActive }" :to="item.route" custom>
        <!--        <a :href="href" v-bind="props.action" @click="navigate">-->
        <!--          <span :class="item.icon"/>-->
        <!--          <span>{{ item.label }}</span>-->
        <Button :label="item.label as string"
                size="small"
                variant="text"
                :class="`${isActive ? 'active': ''}`"
                @click="navigate"></Button>
        <!--        </a>-->
      </router-link>
      <!--      <a v-else v-ripple :href="item.url" :target="item.target" v-bind="props.action">-->
      <!--        <span :class="item.icon"/>-->
      <!--        <span>{{ item.label }}</span>-->
      <!--        <span v-if="hasSubmenu" class="pi pi-fw pi-angle-down"/>-->
      <!--      </a>-->
    </template>
    <template #end>
      <!--      <div class="flex items-center gap-2">-->
      <!--        <Avatar image="/images/avatar/amyelsner.png" shape="circle" />-->
      <!--      </div>-->
            <Button label="Logout" severity="secondary" @click="auth.logout()"/>
    </template>
  </Menubar>
</template>

<style scoped>
.active {
  background-color: var(--p-highlight-background);
}
</style>
