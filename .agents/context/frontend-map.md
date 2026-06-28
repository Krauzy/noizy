# Frontend Map

## Project

Frontend root: `frontend/`.

Angular project name: `noizy-frontend`.

The Angular manifest currently uses:

- `@angular/*`: `20.3.25`.
- TypeScript: `~5.9.3`.
- Build output: `dist/noizy-frontend`.
- Assets: `src/favicon.svg` and `src/assets`.
- Styles: `src/styles.scss`.

## App layout

Main source root: `frontend/src/app`.

- `pages/`: route-level pages.
- `shared/`: reusable UI components such as sidebar, header, track list, music/album/artist cards, audio player, loading spinner, empty state.
- `core/services/`: API and state services for auth, tracks, albums, artists, playlists, upload, and player.
- `core/guards/`: auth and role guards.
- `core/interceptors/`: JWT auth interceptor and mock backend interceptor.
- `core/models/`: music domain models.

## Routes

Routes in `frontend/src/app/app.routes.ts`:

- `/`: home.
- `/login`, `/register`.
- `/artists/:id`.
- `/tracks/:id`.
- `/albums/:id`.
- `/playlists/:id`.
- `/playlists`: auth required.
- `/liked`: auth required.
- `/upload`: auth plus artist role required.
- `/settings`: auth required.

## Runtime modes

- `npm start`: Angular dev server on `0.0.0.0:4200`, using normal environment.
- `npm run start:mock`: Angular dev server on `0.0.0.0:4200`, using `environment.mock.ts` and mock backend interceptor behavior.
- Docker frontend is served by nginx and exposed on host port `8081`.

## Branding and assets

- Logo: `frontend/src/assets/noizy-logo.svg`.
- Favicon: `frontend/src/favicon.svg`.
- Sidebar renders the logo from `assets/noizy-logo.svg`.
- Keep Angular asset wiring in `frontend/angular.json` when changing asset paths.

## API coupling

Frontend services use environment URLs:

- Local dev API base: `http://localhost:8080`.
- API URL: `http://localhost:8080/api`.
- Docker nginx proxies `/api/` to backend `http://backend:8080/api/`.

When changing backend endpoint shapes, update services, models, mocks, and route/page flows together.

## Current shared UI flows

- `shared/notifications`: global toast notifications wired through `errorNotificationInterceptor`.
- `shared/playlist-modal`: shared modal for adding a track to an existing playlist or creating one, opened by track cards, track lists, and the player `+`.
- `shared/audio-player`: floating translucent player; track and artist names link to detail pages; loop icon changes by loop mode; playback is blocked for unauthenticated users in `PlayerService`.
- Global header search lists tracks first and artists second.
- Upload page validates audio/image types, previews the cover, and displays upload progress from HTTP events.
