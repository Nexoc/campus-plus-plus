// src/main.ts
//
// Application entry point.
// Responsible for:
// - creating the Vue application instance
// - registering global plugins (Pinia, Router)
// - configuring global error handling
// - mounting the app to the DOM
//
// IMPORTANT:
// - No business logic here
// - No feature-specific logic
// - This file wires together application infrastructure

import { createPinia } from 'pinia'
import { createApp } from 'vue'

import router from '@/app/router'
import '@/app/styles/main.css'
import { logger } from '@/shared/utils/logger'
import App from './App.vue'

import { initTheme } from '@/shared/theme/theme'


// --------------------------------------------------
// Create Vue application instance
// --------------------------------------------------
const app = createApp(App)

// --------------------------------------------------
// Initialize global theme
// --------------------------------------------------
// Must run BEFORE mount so correct theme is applied
// before first render (no flash)
initTheme()

// --------------------------------------------------
// Register global plugins
// --------------------------------------------------
//
// Pinia -> global state management
// Router -> application navigation
//
app.use(createPinia())
app.use(router)

// --------------------------------------------------
// Global Vue error handler
// --------------------------------------------------
//
// This handler catches ALL uncaught Vue errors:
// - errors in components
// - errors in lifecycle hooks
// - render errors
//
// It does NOT replace try/catch for business logic.
// Its purpose is centralized error visibility.
//
app.config.errorHandler = (err, _instance, info) => {
  // Errors should always be logged,
  // even in production environments.
  logger.error('Vue error:', err)

  // Additional context about where the error occurred
  logger.error('Vue error info:', info)
}

// --------------------------------------------------
// Mount application to DOM
// --------------------------------------------------
app.mount('#app')
