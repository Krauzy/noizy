# Validation Runbook

Use the narrowest validation that covers the behavioral surface changed.

## Backend

```powershell
cd backend
.\gradlew.bat test
```

Use this for changes in Kotlin source, Flyway migrations, security behavior, S3/messaging adapters, DTOs, mappers, or controllers.

## Frontend

```powershell
cd frontend
npm run build
```

Use this for Angular source, routes, services, components, SCSS, environments, assets, and build config.

```powershell
cd frontend
npm test
```

Use this when frontend tests are available and the change affects component/service behavior.

## Infra

```powershell
cd infra/terraform
terraform fmt -check -recursive
```

Use this for Terraform formatting. Full Terraform init/validate may require provider downloads and can be run in Docker if host provider download is unreliable.

## Local full stack

```powershell
docker compose up -d --build
```

Then check:

- Backend health: `http://localhost:8080/actuator/health`.
- Frontend: `http://localhost:8081/`.
- Proxied API from frontend origin, especially `GET /api/tracks?size=1`.
- Login with `admin@noizy.local` / `password123`.
- Audio streaming with `Range` header returns `206` and `Content-Range`.

## Previous proven broad-change checks

For prior broad repo-wide work, the proven validation set included backend tests, frontend build/audit, Terraform formatting/validation, Docker Compose rebuild, backend health, frontend proxy, admin login, and Range streaming.

Do not run the full stack for tiny doc-only or agent-only changes unless needed.
