import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// Dev: run `npm run dev` here (port 5173) alongside the Spring Boot app
// (port 8080). API and websocket traffic is proxied so the UI works without
// CORS changes. Build: `npm run build` emits the production bundle straight
// into Spring Boot's static resources, which the jar then serves.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      "/redis-chat": {
        target: "http://localhost:8080",
        ws: true,
        changeOrigin: true,
      },
      "/message": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
      "/management": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
  build: {
    outDir: "../src/main/resources/static",
    emptyOutDir: true,
  },
});
