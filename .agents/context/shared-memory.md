# Shared Memory From Prior Noizy Work

This file condenses reusable project knowledge imported from Codex shared memory for this checkout.

## Rebrand context

The project was previously renamed repo-wide from Soundly to Noizy. That work touched:

- Backend package root: `com.noizy`.
- Backend config prefix: `noizy.*`.
- Frontend project name: `noizy-frontend`.
- Angular output path: `dist/noizy-frontend`.
- Compose, LocalStack, Terraform, Kubernetes, README/docs, seed data, and admin email.
- Branding assets: `frontend/src/assets/noizy-logo.svg` and `frontend/src/favicon.svg`.

For future rename/branding work, start with a repo-wide search for all name variants, then update source packages, config prefixes, infra identifiers, docs, UI copy, seed data, and validation together.

## Behavioral expectations preserved by the rename

Noizy should preserve:

- JWT auth.
- Public health check.
- S3 audio streaming with Range support.
- Flyway migrations and seed data.
- SQS/SNS event publishing.
- Frontend flows for player, auth, search, playlists, likes, upload, and profile/history.

## Known local validation path

The prior broad validation used:

- `docker compose up -d --build`.
- `http://localhost:8080/actuator/health`.
- `http://localhost:8081/`.
- `GET /api/tracks?size=1` through the frontend proxy.
- Admin login through the frontend proxy.
- Audio streaming with Range returning `206` plus `Content-Range`.
- `terraform fmt -check -recursive`.
- `npm audit --omit=dev`.
- Gradle backend tests.

## Previous failure modes

- Broad rename work can look complete while stale package paths, build configs, infra names, or seed values still break runtime behavior. Use repo-wide search and stack validation.
- Old Compose volumes can keep stale data after renames. Current local volumes are `noizy-postgres-data` and `noizy-localstack-data`.
- Host-side Terraform provider downloads were unreliable before; Dockerized Terraform validation worked.
