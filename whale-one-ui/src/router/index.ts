import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth.ts'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      props: (route) => ({ redirect: route.query.redirect }),
    },
    {
      path: '/projects',
      name: 'projects',
      children: [
        {
          path: '',
          name: 'project-list',
          component: () => import('@/views/project/ProjectListView.vue'),
        },
        {
          path: 'new',
          name: 'project-new',
          component: () => import('@/views/project/ProjectNewView.vue'),
        },
        {
          path: ':id',
          name: 'project-detail',
          component: () => import('@/views/project/ProjectView.vue'),
          props: (route) => ({ id: parseInt(route.params.id as string) }),
        },
      ],
    },
    {
      path: '/deployments',
      name: 'deployments',
      children: [
        {
          path: '',
          name: 'deployment-list',
          component: () => import('@/views/deployment/DeploymentListView.vue'),
        },
        {
          path: 'new',
          name: 'deployment-new',
          component: () => import('@/views/deployment/DeploymentNewView.vue'),
        },
        {
          path: ':id',
          name: 'deployment-detail',
          component: () => import('@/views/deployment/DeploymentView.vue'),
          props: (route) => ({ id: parseInt(route.params.id as string) }),
        },
      ],
    },
    {
      path: '/equipment',
      name: 'equipment',
      children: [
        {
          path: '',
          name: 'equipment-list',
          component: () => import('@/views/equipment/EquipmentListView.vue'),
        },
        {
          path: 'new',
          name: 'equipment-new',
          component: () => import('@/views/equipment/EquipmentNewView.vue'),
        },
        {
          path: ':id',
          name: 'equipment-detail',
          component: () => import('@/views/equipment/EquipmentView.vue'),
          props: (route) => ({ id: parseInt(route.params.id as string) }),
        },
        {
          path: 'types',
          name: 'equipment-type-list-parent',
          children: [
            {
              path: '',
              name: 'equipment-type-list',
              component: () => import('@/views/equipment/types/EquipmentTypeListView.vue'),
            },
            {
              path: 'new',
              name: 'equipment-type-new',
              component: () => import('@/views/equipment/types/EquipmentTypeNewView.vue'),
            },
            {
              path: ':id',
              name: 'equipment-type-detail',
              component: () => import('@/views/equipment/types/EquipmentTypeView.vue'),
              props: (route) => ({ id: parseInt(route.params.id as string) }),
            },
          ],
        },
      ],
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue'),
    },
  ],
})

router.beforeEach(async (to, from) => {
  const auth = useAuthStore()
  if (!auth.isAuthenticated() && to.name !== 'login') {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
})

export default router
