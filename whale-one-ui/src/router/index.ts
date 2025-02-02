import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import DepView from '@/views/DepView.vue'
import ProjectView from "@/views/ProjectView.vue";
import EquipmentView from "@/views/EquipmentView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/projects',
      name: 'projects',
      component: ProjectView,
    },
    {
      path: '/deployments',
      name: 'deployments',
      component: DepView,
    },
    {
      path: '/equipment',
      name: 'equipment',
      component: EquipmentView,
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

export default router
