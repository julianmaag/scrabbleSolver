import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    // Keeps requests same-origin in dev, so no CORS config is needed on the Spring backend.
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
})
