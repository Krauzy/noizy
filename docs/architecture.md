# Noizy Architecture

Noizy is a fullstack music streaming monorepo with:

- Kotlin/Spring Boot backend exposing JWT-protected REST APIs.
- PostgreSQL persistence managed by Flyway migrations.
- S3 object storage for audio and artwork.
- SQS/SNS event publishing for playback, upload, playlist, and registration events.
- Angular frontend with a dark UI and yellow primary theme.
- Docker Compose for local Postgres, Redis, LocalStack, backend, and frontend.
- Terraform and Kubernetes manifests prepared for AWS EKS.

## Runtime Flow

```text
Angular SPA
  -> REST calls with Bearer JWT
  -> Spring Boot controllers
  -> application services
  -> JPA repositories / PostgreSQL
  -> S3 for audio objects
  -> SNS/SQS for async events
```

## Backend Layers

```text
interfaces/rest -> application/service -> infrastructure/persistence
                                 \-> infrastructure/aws
                                 \-> infrastructure/messaging
```

Controllers only translate HTTP input and output. Business operations live in services. Persistence and cloud integrations stay behind infrastructure adapters.
