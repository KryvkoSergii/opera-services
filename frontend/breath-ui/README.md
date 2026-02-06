# breath-ui \- Frontend Module

breath-ui is the frontend module of the Breath project, built with React and TypeScript. It provides the user interface components and pages for interacting with the Breath backend services.

## Tech stack

- React (SPA)
- TypeScript
- JavaScript (tooling)
- npm (package manager)
- Build tool: [Vite]

## Project structure

Common layout (adjust to actual structure):

- `src/`
  - `components/` - reusable UI components
  - `pages/` or `routes/` - top-level views
  - `app/` - app related utils
  - `api/` - REST client.
  - `auth/` - authentication logic
  - `scripts/` - supporting scripts (e.g. codegen)
- `public/` \- static public files
- `package.json` \- npm scripts and dependencies
- `tsconfig.json` \- TypeScript configuration
- `vite.config.*` - bundler config (if applicable)

## Prerequisites

- Node.js \[version] or newer
- npm \[version] (or yarn/pnpm)
- Breath backend running (optional but recommended for full functionality)

## Installation

From the `breath-ui` module directory:

```bash
npm install
```

## Development

Start the development server:

```bash
npm run dev
```

Then open the port shown in the terminal in a browser.

## Building for production

```bash
npm run build
```

The production artifacts will be emitted to the `dist/` or `build/` folder (depending on tooling).

## Linting and formatting

```bash
npm run lint
npm run format
```

## Environment configuration

Create a `.env` file based on `.env.example` (if present). Common variables:

- `VITE_API_BASE_URL` - base URL of the Breath backend (e.g. `http://localhost:8080`)
- `NODE_ENV` \- environment (development/production)