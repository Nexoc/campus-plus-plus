// src/app/router/index.ts

import { useAuthStore } from '@/modules/auth/store/auth.store'
import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

import LoginPage from '@/modules/auth/pages/LoginPage.vue'
import HomePage from '@/modules/home/pages/HomePage.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'login',
    component: LoginPage,
    meta: {
      public: true,
      requiresGuest: true,
    },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/modules/auth/pages/RegisterPage.vue'),
    meta: {
      requiresGuest: true,
    },
  },

  {
    path: '/',
    name: 'home',
    component: HomePage,
    meta: { requiresAuth: true },
  },
  {
    path: '/account/change-password',
    name: 'ChangePassword',
    component: () =>
      import('@/modules/account/pages/ChangePasswordPage.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/admin/users',
    name: 'AdminUsers',
    component: () =>
      import('@/modules/admin/pages/AdminUsersPage.vue'),
    meta: {
      requiresAuth: true,
      requiresAdmin: true,
    },
  },
  {
    path: '/account',
    name: 'Account',
    component: () =>
      import('@/modules/account/pages/AccountPage.vue'),
    meta: { requiresAuth: true },
  },
  {
  path: '/courses',
  name: 'Courses',
  component: () => import('@/modules/courses/pages/CoursesPage.vue'),
  meta: { requiresAuth: true },
},


]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// --------------------------------------------------
// GLOBAL AUTH / ADMIN GUARD
// --------------------------------------------------
router.beforeEach((to) => {
  const auth = useAuthStore()

  // --------------------------------------------
  // Guest-only pages (login, register)
  // --------------------------------------------
  if (to.meta.requiresGuest && auth.isAuthenticated) {
    return { name: 'home' }
  }

  // --------------------------------------------
  // Public pages (no auth required)
  // --------------------------------------------
  if (to.meta.public) {
    return true
  }

  // --------------------------------------------
  // Authenticated pages
  // --------------------------------------------
  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    return { name: 'login' }
  }

  // --------------------------------------------
  // Admin-only pages
  // --------------------------------------------
  if (to.meta.requiresAdmin && !auth.isAdmin) {
    return { name: 'home' }
  }

  return true
})



export default router
