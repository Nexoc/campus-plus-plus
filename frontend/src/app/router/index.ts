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
    meta: { public: true },
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
      requiresModerator: true,
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
    path: '/favourites',
    name: 'Favourites',
    component: () =>
      import('@/modules/favourites/pages/FavouritesPage.vue'),
    meta: { requiresAuth: true },
  },
  {
  path: '/courses',
  name: 'Courses',
  component: () => import('@/modules/courses/pages/CoursesPage.vue'),
  meta: { public: true },
},
  {
  path: '/programs',
  name: 'StudyPrograms',
  component: () => import('@/modules/studyprograms/pages/StudyProgramsPage.vue'),
  meta: { public: true },
},

  {
    path: '/programs/:id/:slug?',
    name: 'StudyProgramDetail',
    component: () => import('@/modules/studyprograms/pages/StudyProgramDetailPage.vue'),
    meta: { public: true },
  },

  {
    path: '/courses/:id/:slug?',
    name: 'CourseDetail',
    component: () => import('@/modules/courses/pages/CourseDetailPage.vue'),
    meta: { public: true },
  },

  {
    path: '/courses/new',
    name: 'CourseCreate',
    component: () => import('@/modules/courses/pages/CourseCreatePage.vue'),
    meta: { requiresAuth: true, requiresModerator: true },
  },

  {
    path: '/courses/:id/edit',
    name: 'CourseEdit',
    component: () => import('@/modules/courses/pages/CourseEditPage.vue'),
    meta: { requiresAuth: true, requiresModerator: true },
  },

  {
    path: '/programs/new',
    name: 'StudyProgramCreate',
    component: () => import('@/modules/studyprograms/pages/StudyProgramCreatePage.vue'),
    meta: { requiresAuth: true, requiresModerator: true },
  },

  {
    path: '/programs/:id/edit',
    name: 'StudyProgramEdit',
    component: () => import('@/modules/studyprograms/pages/StudyProgramEditPage.vue'),
    meta: { requiresAuth: true, requiresModerator: true },
  },

  {
    path: '/moderation/reviews',
    name: 'ModerationReviews',
    component: () => import('@/modules/reviews/pages/ModerationReviewsPage.vue'),
    meta: {
      requiresAuth: true,
      requiresModerator: true,
    },
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
  // Moderator-only pages
  // --------------------------------------------
  if (to.meta.requiresModerator && !auth.isModerator) {
    return { name: 'home' }
  }

  return true
})



export default router
