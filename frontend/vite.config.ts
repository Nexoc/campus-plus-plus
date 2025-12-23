// vite.config.ts
//
// Vite configuration file.
// This file runs in a Node.js environment (NOT in the browser).
//
// Responsibilities:
// - register Vite plugins
// - configure module resolution
// - define path aliases
//
// IMPORTANT:
// - This file contains build-time configuration only
// - No application or business logic should be placed here

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

// --------------------------------------------------
// Export Vite configuration
// --------------------------------------------------
export default defineConfig({
  // --------------------------------------------------
  // Plugins
  // --------------------------------------------------
  //
  // Vue plugin enables Vue SFC (.vue) support.
  //
  plugins: [vue()],
  server: {
  host: "0.0.0.0",
  port: 80
},


  // --------------------------------------------------
  // Module resolution
  // --------------------------------------------------
  //
  // Define path aliases to simplify imports.
  //
  // Alias:
  //   @ -> /src
  //
  // This allows imports like:
  //   import Foo from '@/modules/foo/Foo.vue'
  //
  // NOTE:
  // - fileURLToPath + import.meta.url is required
  //   because this config runs in ES module mode.
  //
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
})
