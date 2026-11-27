# react-ui

Astryx-themed React group-chat UI for spring-redis-websocket. Built with
Vite; served by the Spring Boot app from `src/main/resources/static`.

## Develop (UI + backend side by side)

1. Start Redis (see `src/main/docker/docker-compose.yml`).
2. Start Spring Boot: `./mvnw spring-boot:run` (serves API on `:8080`).
3. In another terminal: `cd react-ui && npm install && npm run dev`.
4. Open http://localhost:5173 — `/redis-chat`, `/message`, and `/management`
   are proxied to the backend, so connect/send behave like production.

## Build

- `npm run build` — emits the production bundle into
  `src/main/resources/static` (generated files are git-ignored).
- `./mvnw package` — builds the UI via `frontend-maven-plugin`, then the
  jar; the jar serves the UI at `/` with no extra backend changes
  (`WebHttpHandler` already serves `classpath:/static/index.html`).

## Chat surface

One group room: anyone who connects joins. The header shows the backend
platform, the online-users badge, and the total message count; Connect /
Disconnect toggle the websocket; the feed renders every broadcast message
(id, text, sending host) plus join/leave notices; the composer sends over the
websocket and the Send buttons cover both the websocket and HTTP POST paths.
