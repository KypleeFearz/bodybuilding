import { svelte } from '@sveltejs/vite-plugin-svelte';
import { defineConfig } from 'vitest/config';

const backend = process.env.BACKEND_URL ?? 'http://localhost:8080';

// https://vite.dev/config/
export default defineConfig({
  plugins: [svelte()],
  server: {
    port: 5173,
    // The dev server forwards API calls to Spring Boot, so no CORS setup is needed.
    proxy: {
      '/api': backend,
      '/swagger-ui': backend,
      '/v3/api-docs': backend,
    },
  },
  test: {
    include: ['src/**/*.test.ts'],
    environment: 'node',
  },
});
