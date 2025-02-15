import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/projects',
      name: 'projects',
      children: [
        {
          path: '',
          name: 'project-list',
          component: () => import('@/views/ProjectsView.vue')
        },
        {
          path: 'new',
          name: 'project-new',
          component: () => import('@/views/ProjectNewView.vue')
        },
        {
          path: ':id',
          name: 'project-detail',
          component: () => import('@/views/ProjectView.vue'),
          props: route => ({ id: parseInt(route.params.id as string) })
        }
      ]
    },
    {
      path: '/deployments',
      name: 'deployments',
      children: [
        {
          path: '',
          name: 'deployment-list',
          component: () => import('@/views/DeploymentsView.vue')
        },
        {
          path: 'new',
          name: 'deployment-new',
          component: () => import('@/views/DeploymentNewView.vue')
        },
        {
          path: ':id',
          name: 'deployment-detail',
          component: () => import('@/views/DeploymentView.vue'),
          props: route => ({ id: parseInt(route.params.id as string) })
        }
      ]
    },
    {
      path: '/equipment',
      name: 'equipment',
      children: [
        {
          path: '',
          name: 'equipment-list',
          component: () => import('@/views/equipment/EquipmentListView.vue')
        },
        {
          path: 'new',
          name: 'equipment-new',
          component: () => import('@/views/equipment/EquipmentNewView.vue')
        },
        {
          path: ':id',
          name: 'equipment-detail',
          component: () => import('@/views/equipment/EquipmentView.vue'),
          props: route => ({ id: parseInt(route.params.id as string) })
        },
        {
          path: 'types',
          name: 'equipment-type-list-parent',
          children: [
            {
              path: '',
              name: 'equipment-type-list',
              component: () => import('@/views/equipment/types/EquipmentTypeListView.vue')
            },
            {
              path: 'new',
              name: 'equipment-type-new',
              component: () => import('@/views/equipment/types/EquipmentTypeNewView.vue')
            },
            {
              path: ':id',
              name: 'equipment-type-detail',
              component: () => import('@/views/equipment/types/EquipmentTypeView.vue'),
              props: route => ({ id: parseInt(route.params.id as string) })
            }
          ]
        }
      ]
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue')
    }
  ]
})

export default router
