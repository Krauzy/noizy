# Backend Map

## Source root

Backend Kotlin package root: `backend/src/main/kotlin/com/noizy`.

Layer layout:

- `interfaces/rest`: HTTP controllers and request mapping.
- `interfaces/dto`: API DTOs.
- `interfaces/mapper`: entity-to-response mapping.
- `application/service`: business behavior.
- `domain/event`: event model and event types.
- `domain/exception`: domain exceptions.
- `infrastructure/security`: JWT filter, service, and user principal.
- `infrastructure/config`: Spring security and AWS clients.
- `infrastructure/aws`: S3 storage adapter.
- `infrastructure/messaging`: SNS/SQS publishing and consumption.
- `infrastructure/persistence/entity`: JPA entities.
- `infrastructure/persistence/repository`: Spring Data repositories.

## Controllers

REST controllers live under `interfaces/rest`:

- `AuthController.kt`
- `UserController.kt`
- `ArtistController.kt`
- `AlbumController.kt`
- `TrackController.kt`
- `PlaylistController.kt`
- `PlaybackController.kt`
- `GlobalExceptionHandler.kt`

Main endpoint families include `/api/auth`, `/api/users`, `/api/artists`, `/api/albums`, `/api/tracks`, `/api/playlists`, and `/api/playback`.

## Security

`SecurityConfig.kt` configures stateless JWT auth.

- Public: register, login, Swagger/OpenAPI, `/actuator/health`.
- Public GET reads: artists, albums, tracks, public playlists, individual playlists.
- Authenticated: liked tracks, current user's playlists, playback/history style operations, most writes.
- Track upload requires `ARTIST` or `ADMIN`.
- CORS allows the configured `noizy.frontend-origin`, `http://localhost:5173`, and `http://localhost:8081`.
- CORS exposes `Content-Range`, `Accept-Ranges`, and `Content-Length` for audio streaming.

## Track and streaming behavior

`TrackController.kt` delegates track operations to `TrackService.kt`.

- `POST /api/tracks/upload`: multipart upload with required `audio`, optional `cover`, and artist/admin role.
- `GET /api/tracks`: paginated list.
- `GET /api/tracks/search?query=`: paginated search.
- `GET /api/tracks/liked`: current user's liked tracks.
- `GET /api/tracks/{id}/stream`: streams from S3 and honors optional `Range`.
- `GET /api/tracks/{id}/cover`: streams cover image from S3.
- `POST /api/tracks/{id}/like` and `DELETE /api/tracks/{id}/like`: current user's like state.
- `GET /api/tracks/{id}/comments` and `POST /api/tracks/{id}/comments`: track discussion, with writes authenticated.

`TrackService.stream` increments play count, records playback history for authenticated users, publishes `TRACK_PLAYED`, reads S3 audio, and returns `206` plus `Content-Range` when a valid Range header is present. `S3StorageService.getAudio` caps open-ended byte ranges such as `bytes=0-` to 1 MiB chunks so the browser does not need to download the full object at once.

`TrackResponse` includes `liked`, computed from the authenticated user when a JWT is present.

## User profile media

`UserController.kt` exposes:

- `GET /api/users/me`: current user.
- `POST /api/users/me/avatar`: multipart image upload for the current user's avatar.
- `GET /api/users/{id}/avatar`: public avatar image stream from S3.

`UserResponse` includes `avatarS3Key` and `avatarUrl`.

## Events

`NoizyEventType` currently has:

- `TRACK_PLAYED`
- `TRACK_UPLOADED`
- `PLAYLIST_CREATED`
- `USER_REGISTERED`

Events are published through `EventPublisher.kt`, backed by SNS/SQS when configured.

## Configuration

`backend/src/main/resources/application.yml` owns runtime defaults:

- `SERVER_PORT`, default `8080`.
- Postgres datasource defaults to local `noizy` database.
- Redis defaults to localhost:6379.
- `noizy.frontend-origin`, default `http://localhost:4200`.
- `noizy.jwt.secret` and expiration.
- AWS region, endpoint, S3 bucket names, SQS queue URL, SNS topic ARN.

Flyway migrations live in `backend/src/main/resources/db/migration`.
