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
        {
          path: 'map',
          name: 'deployment-map',
          component: () => import('@/views/deployment/DeploymentMapView.vue'),
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
              component: () => import('@/views/admin/equipment/EquipmentTypeListView.vue'),
            },
            {
              path: 'new',
              name: 'equipment-type-new',
              component: () => import('@/views/admin/equipment/EquipmentTypeNewView.vue'),
            },
            {
              path: ':id',
              name: 'equipment-type-detail',
              component: () => import('@/views/admin/equipment/EquipmentTypeView.vue'),
              props: (route) => ({ id: parseInt(route.params.id as string) }),
            },
          ],
        },
      ],
    },
    {
      path: '/administration',
      name: 'administration',
      children: [
        {
          path: 'analysis/types',
          name: 'analysis-type-list',
          component: () => import('@/views/admin/analysis/AnalysisTypeListView.vue'),
        },
        {
          path: 'analysis/types/new',
          name: 'analysis-type-new',
          component: () => import('@/views/admin/analysis/AnalysisTypeNewView.vue'),
        },
        {
          path: 'analysis/types/:id',
          name: 'analysis-type-detail',
          component: () => import('@/views/admin/analysis/AnalysisTypeView.vue'),
          props: (route) => ({ id: parseInt(route.params.id as string) }),
        },
        {
          path: 'equipment/types',
          name: 'equipment-type-list',
          component: () => import('@/views/admin/equipment/EquipmentTypeListView.vue'),
        },
        {
          path: 'equipment/types/new',
          name: 'equipment-type-new',
          component: () => import('@/views/admin/equipment/EquipmentTypeNewView.vue'),
        },
        {
          path: 'equipment/types/:id',
          name: 'equipment-type-detail',
          component: () => import('@/views/admin/equipment/EquipmentTypeView.vue'),
          props: (route) => ({ id: parseInt(route.params.id as string) }),
        },
        {
          path: 'users',
          name: 'user-list',
          component: () => import('@/views/admin/user/UserListView.vue'),
        },
        {
          path: 'users/new',
          name: 'user-new',
          component: () => import('@/views/admin/user/UserNewView.vue'),
        },
        {
          path: 'users/:id',
          name: 'user-detail',
          component: () => import('@/views/admin/user/UserView.vue'),
          props: (route) => ({ id: parseInt(route.params.id as string) }),
        },
      ],
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
