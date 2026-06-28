[![wakatime](https://wakatime.com/badge/github/Krauzy/noizy.svg)](https://wakatime.com/badge/github/Krauzy/noizy)

# Noizy

Noizy is a fullstack music streaming platform inspired by common streaming workflows without using protected brands, names, or assets.

<img width="1392" height="952" alt="image" src="https://github.com/user-attachments/assets/3b0c9c61-fa64-4e12-9960-bf1b29e74a59" />

## Stack

- Backend: Kotlin, Spring Boot 3, Spring Web, Spring Security, JWT, Spring Data JPA, Flyway, PostgreSQL, Redis, AWS SDK for S3/SQS/SNS, OpenAPI.
- Frontend: Angular 17, standalone components, Angular Router, Reactive Forms, HttpClient, JWT interceptor, auth guard, SCSS.
- Local: Docker Compose, LocalStack, PostgreSQL, Redis.
- Cloud: Terraform for VPC, EKS, ECR, S3, RDS PostgreSQL, SQS, SNS, IAM, ElastiCache Redis, CloudWatch logs.
- Kubernetes: EKS-ready manifests for deployments, services, ingress, config, secret, HPA, namespace, and service account.

## Structure

```text
backend/                 Kotlin Spring Boot API
frontend/                Angular web player
infra/terraform/         AWS infrastructure
infra/kubernetes/        EKS manifests
docker/localstack/       Local AWS bootstrap script
docs/                    Architecture notes
docker-compose.yml       Local full stack
```

## Architecture

```text
Browser
  -> Angular SPA
  -> /api REST
  -> Spring Boot services
  -> PostgreSQL via JPA
  -> S3 for audio and covers
  -> SNS/SQS for async events
```

Backend source follows:

```text
com/noizy/
  application/service
  domain/event
  domain/exception
  infrastructure/aws
  infrastructure/config
  infrastructure/messaging
  infrastructure/persistence
  infrastructure/security
  interfaces/dto
  interfaces/mapper
  interfaces/rest
```

## Local Run With Docker Compose

```bash
docker compose up --build
```

Services:

- Frontend: http://localhost:8081
- Backend API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- LocalStack edge: http://localhost:4566
- PostgreSQL: localhost:5432
- Redis: localhost:6379

The `localstack-init` service creates:

- `noizy-tracks` S3 bucket
- `noizy-images` S3 bucket
- `noizy-track-events` SQS queue
- `noizy-track-events` SNS topic
- Seed placeholder audio objects for seeded tracks

Seed admin account:

- Email: `admin@noizy.local`
- Password: `password123`

## Run Backend Locally

Start Postgres, Redis, and LocalStack first:

```bash
docker compose up postgres redis localstack localstack-init
```

Then run:

```bash
cd backend
gradle bootRun
```

Useful environment values are listed in `.env.example`.

## Run Frontend Locally

```bash
cd frontend
npm install
npm start
```

Frontend dev server runs on http://localhost:4200 and calls the backend at `http://localhost:8080/api`.

## Tests

Backend:

```bash
cd backend
gradle test
```

Frontend:

```bash
cd frontend
npm test
```

## Main API Endpoints

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`
- `GET /api/artists`
- `GET /api/albums`
- `GET /api/albums/{id}/tracks`
- `POST /api/tracks/upload`
- `GET /api/tracks`
- `GET /api/tracks/search?query=`
- `GET /api/tracks/{id}/stream`
- `POST /api/tracks/{id}/like`
- `GET /api/tracks/liked`
- `POST /api/playlists`
- `GET /api/playlists/me`
- `GET /api/playlists/public`
- `POST /api/playlists/{id}/tracks/{trackId}`
- `POST /api/playback/{trackId}`
- `GET /api/playback/history`

## Terraform

```bash
cd infra/terraform
terraform init
terraform plan -var="database_password=replace-with-strong-password"
terraform apply -var="database_password=replace-with-strong-password"
```

Outputs include ECR repositories, S3 bucket names, RDS endpoint, Redis endpoint, SQS URL, SNS ARN, and backend IRSA role ARN.

## Kubernetes Deploy

1. Build and push images to the ECR URLs from Terraform outputs.
2. Replace placeholder image registry/account values in `infra/kubernetes/*.yaml`.
3. Replace secret values in `infra/kubernetes/secret.yaml` through your secret manager or deployment pipeline.
4. Apply manifests:

```bash
kubectl apply -f infra/kubernetes/
```

The ingress uses AWS ALB annotations and expects the AWS Load Balancer Controller installed on the EKS cluster.

## Technical Decisions

- Audio is streamed through the backend from S3 so auth, range headers, metrics, and playback events can be centralized.
- JWT keeps API sessions stateless and deploy-friendly for Kubernetes.
- Flyway owns schema creation and seed data.
- SNS is configured for fanout, with SQS as the internal event queue.
- Angular uses a service-driven player so pages can queue and play tracks consistently.

## Next Steps

- Add waveform previews and richer recommendations.
- Move runtime secrets to AWS Secrets Manager or External Secrets Operator.
- Add presigned upload mode for very large audio files.
- Add CI pipelines for image build, tests, Terraform validation, and Kubernetes deploy.
