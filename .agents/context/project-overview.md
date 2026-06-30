# Project Overview and Stack

Noizy is a fullstack music streaming monorepo.

## Current stack

- Backend: Kotlin 1.9.25, Spring Boot 3.3.5, Java 21, Spring Web, Spring Security, JWT, Spring Data JPA, Flyway, PostgreSQL, Redis, AWS SDK v2 for S3/SQS/SNS, OpenAPI.
- Frontend: Angular application named `noizy-frontend`, with Angular packages currently at `20.3.25`, standalone components, Angular Router, Reactive Forms, HttpClient, guards, interceptors, and SCSS.
- Local runtime: Docker Compose with Postgres 16, Redis 7, LocalStack 3.8, backend on 8080, frontend on 8081.
- Cloud/IaC: Terraform for AWS VPC, EKS, ECR, S3, RDS PostgreSQL, SQS, SNS, IAM, ElastiCache Redis, and CloudWatch logs.
- Kubernetes: manifests for namespace, config, secret, service account, deployments, services, ingress, and HPA.

## Repo structure

- `backend/`: Spring Boot API source, Gradle wrapper, Flyway migrations, tests.
- `frontend/`: Angular web player, nginx proxy config, Dockerfile, package lock.
- `infra/terraform/`: AWS infrastructure definitions.
- `infra/kubernetes/`: EKS-ready manifests.
- `docker/localstack/`: Local AWS bootstrap script.
- `docs/architecture.md`: concise architecture description.
- `.agents/`: project-local agent config and context RAG.

## Runtime flow

Browser -> Angular SPA -> `/api` REST -> Spring controllers -> application services -> JPA/PostgreSQL -> S3 for audio and cover objects -> SNS/SQS for async events.

## Common facts

- Seed admin: `admin@noizy.local` / `password123`.
- Backend health: `http://localhost:8080/actuator/health`.
- Frontend via Docker: `http://localhost:8081/`.
- Frontend dev server: `http://localhost:4200`.
- Swagger UI: `http://localhost:8080/swagger-ui.html`.
- LocalStack edge: `http://localhost:4566`.

## Drift notes

- `README.md` says Angular 17, but `frontend/package.json` currently uses Angular `20.3.25`; prefer manifests over README for dependency facts.
- `.agents/context/` is an orientation cache. Verify live files before editing.
