import { defineConfig } from 'vitest/config'

export default defineConfig({
  test: {
    environment: 'jsdom',
    globals: true,
    coverage: {
      provider: 'v8',
      reporter: ['text', 'lcov'],
      include: ['src/api/**', 'src/utils/**'],
      exclude: [
        'src/api/mockDb.ts',
        'src/api/poolsMockDb.ts',
        'src/api/config.ts',
        'src/api/loginApi.ts',
        'src/api/mapsApi.ts',
        'src/api/http.ts',
        'src/api/eventsApi.ts',
        'src/api/supportApi.ts',
        'src/api/groupApi.ts',
        'src/api/albumApi.ts',
        'src/api/marketApi.ts',
        'src/api/tradesApi.ts',
      ],
    },
  },
})