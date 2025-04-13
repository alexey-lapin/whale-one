<script setup lang="ts">
import { computed, type Ref, ref, watchEffect } from 'vue'

import Avatar from 'primevue/avatar'
import Button from 'primevue/button'
import Menubar from 'primevue/menubar'
import TieredMenu from 'primevue/tieredmenu'

import { useColorMode, useCycleList } from '@vueuse/core'

import router from '@/router'
import { useAuthStore } from '@/stores/auth.ts'

import type { MenuItem } from 'primevue/menuitem'

let auth = useAuthStore()

const items: MenuItem[] = [
  {
    label: 'Projects',
    route: '/projects',
  },
  {
    label: 'Deployments',
    route: '/deployments',
  },
  {
    label: 'Equipment',
    route: '/equipment',
  },
  {
    label: 'Administration',
    routeRoot: '/administration',
    visible: auth.hasAuthority('ADMIN'),
    items: [
      {
        label: 'Analysis Types',
        route: '/administration/analysis/types',
      },
      {
        label: 'Equipment Types',
        route: '/administration/equipment/types',
      },
      {
        label: 'Users',
        route: '/administration/users',
      },
    ],
  },
]

const userMenuItems = ref([
  {
    label: 'Settings',
    icon: 'pi pi-user-edit',
    command: () => router.push('/profile'),
  },
  {
    label: 'Logout',
    icon: 'pi pi-sign-out',
    command: () => auth.logout(),
  },
])

const mode = useColorMode({
  emitAuto: true,
})

const { state, next } = useCycleList(['dark', 'light', 'auto'] as const, {
  initialValue: mode,
})

watchEffect(() => (mode.value = state.value))

const modeIcon = computed(() => {
  if (state.value === 'auto') {
    return 'pi pi-desktop'
  } else if (state.value === 'light') {
    return 'pi pi-sun'
  } else if (state.value === 'dark') {
    return 'pi pi-moon'
  }
  return ''
})

const menu: Ref<any> = ref(null)

const toggleMenu = (event: any) => {
  menu.value?.toggle(event)
}
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
      <router-link
        v-if="item.route"
        v-slot="{ href, navigate, isActive }"
        :to="item.route"
        custom
      >
        <!--        <a :href="href" v-bind="props.action" @click="navigate">-->
        <!--          <span :class="item.icon"/>-->
        <!--          <span>{{ item.label }}</span>-->
        <Button
          :label="item.label as string"
          size="small"
          variant="text"
          :class="`${isActive ? 'active' : ''}`"
          @click="navigate"
        />
        <!--        </a>-->
      </router-link>
      <router-link
        v-else-if="item.routeRoot"
        v-slot="{ href, navigate, isActive }"
        :to="item.routeRoot"
        custom
      >
        <Button
          :label="item.label as string"
          icon-pos="right"
          icon="pi pi-angle-down"
          size="small"
          variant="text"
          :class="`${isActive ? 'active' : ''}`"
        />
      </router-link>
    </template>
    <template #end>
      <div class="flex items-central gap-1">
        <Button
          :icon="modeIcon"
          size="small"
          variant="text"
          severity="secondary"
          class="!p-0"
          @click="next()"
        />
        <Avatar
          :label="auth.username?.charAt(0).toUpperCase()"
          shape="circle"
          class="cursor-pointer"
          @click="toggleMenu"
        />
      </div>
      <TieredMenu
        ref="menu"
        :model="userMenuItems"
        :popup="true"
      />
    </template>
  </Menubar>
</template>

<style scoped>
.active {
  background-color: var(--p-highlight-background);
}
</style>
