// src/app/router/index.ts
//
// Application router configuration.
// Responsible for:
// - defining application routes
// - marking routes as public or protected
// - enforcing authentication via a global navigation guard
//
// IMPORTANT:
// - Router contains NO business logic
// - Router decides ONLY navigation access
// - Authentication state is read from the auth store

import { useAuthStore } from '@/modules/auth/store/auth.store'
import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

// --------------------------------------------------
// Route components (pages)
// --------------------------------------------------
//
// Pages are imported directly.
// In larger applications, lazy loading can be applied.
//
import LoginPage from '@/modules/auth/pages/LoginPage.vue'
import RegisterPage from '@/modules/auth/pages/RegisterPage.vue'
import HomePage from '@/modules/home/pages/HomePage.vue'

// --------------------------------------------------
// Route definitions
// --------------------------------------------------
//
// meta.public       -> route accessible without authentication
// meta.requiresAuth -> route requires authenticated user
//
const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'login',
    component: LoginPage,
    meta: { public: true },
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterPage,
    meta: { public: true },
  },
  {
    path: '/',
    name: 'home',
    component: HomePage,
    meta: { requiresAuth: true },
  },
]

// --------------------------------------------------
// Router instance
// --------------------------------------------------
const router = createRouter({
  history: createWebHistory(),
  routes,
})

// --------------------------------------------------
// Global authentication guard
// --------------------------------------------------
//
// This guard runs before every navigation.
// It enforces authentication rules based on route metadata.
//
// Behavior:
// - Public routes are always accessible
// - Protected routes require a valid authentication state
//
router.beforeEach((to) => {
  const authStore = useAuthStore()

  // Allow access to public routes without checks
  if (to.meta.public) {
    return true
  }

  // Redirect unauthenticated users away from protected routes
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    return { name: 'login' }
  }

  // Allow navigation
  return true
})

export default router
