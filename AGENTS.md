# Noizy Agent Instructions

## Startup

Run the local context RAG before broad repository searches:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .agents/scripts/search-context.ps1 -Query "<user request>" -Top 5
```

Use the returned context to choose targeted files and then verify current code with `rg` or direct file reads before editing. If the RAG has no useful hit, fall back to normal repo exploration and update `.agents/context/` when the new knowledge is reusable.

## Project Shape

Noizy is a fullstack music streaming monorepo.

- `backend/`: Kotlin, Spring Boot 3, JPA, Flyway, JWT security, PostgreSQL, Redis, AWS SDK for S3/SQS/SNS.
- `frontend/`: Angular application with standalone components, services, guards, interceptors, SCSS, and a mock backend interceptor.
- `infra/terraform/`: AWS VPC, EKS, ECR, S3, RDS, SQS, SNS, IAM, Redis, and outputs.
- `infra/kubernetes/`: EKS-ready manifests for backend, frontend, ingress, service account, HPA, config, and secret.
- `docker-compose.yml`: local Postgres, Redis, LocalStack, backend, and frontend stack.
- `docs/`: architecture notes.

## Change Rules

- Prefer existing layer boundaries: REST controllers translate HTTP, services hold business behavior, repositories stay under infrastructure, frontend data access stays in `core/services`.
- Keep JWT, Range streaming, Flyway migrations, S3 object keys, SQS/SNS event publishing, and seeded demo data coherent across backend, frontend, Docker, and infra.
- Do not rely only on stale context. The RAG narrows the search, but source files and manifests remain authoritative.
- Preserve project-owned branding assets under `frontend/src/assets/` and Angular asset wiring in `frontend/angular.json`.
- When a change affects a shared behavior, update the relevant `.agents/context/*.md` file and `.agents/context/index.json` so future agents do not rediscover it.

## Validation

Use the narrowest meaningful validation for the change:

- Backend: `cd backend; .\gradlew.bat test`
- Frontend build: `cd frontend; npm run build`
- Frontend tests: `cd frontend; npm test`
- Terraform formatting: `cd infra/terraform; terraform fmt -check -recursive`
- Full local stack: `docker compose up -d --build`

For broad changes, also verify backend health at `http://localhost:8080/actuator/health`, frontend at `http://localhost:8081/`, proxied API calls, auth flows, and audio streaming with Range requests.
