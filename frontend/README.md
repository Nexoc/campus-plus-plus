# Campus++ Frontend

This is the frontend for the Campus++ platform, built as a modern Vue 3 Single Page Application (SPA).

## Features
- Vue 3 + Vite for fast development and builds
- TypeScript support
- Modular structure (modules for courses, study programs, reviews, etc.)
- Communicates with backend via REST API
- Stateless: all authentication is handled by the NGINX gateway and Auth Service
- Responsive, accessible UI

## Project Structure
- `src/` — Main source code
  - `modules/` — Feature modules (courses, study programs, etc.)
  - `app/` — App-wide config, API, router, security, styles
  - `shared/` — Shared components, composables, theme, utils
  - `assets/` — Static assets
- `public/` — Static files served as-is
- `index.html` — Main HTML entry point

## Development
To start the frontend in development mode:

```
npm install
npm run dev
```

## Build for Production
```
npm run build
```

## Deployment
The built static files are served by the NGINX gateway in production.

## Environment
- All API requests are routed through the gateway
- No authentication logic in the frontend; JWT is sent in the `Authorization` header automatically

## Contact
For questions or contributions, see the main Campus++ repository.
