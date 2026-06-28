# Infrastructure and Local Runtime

## Docker Compose

Root file: `docker-compose.yml`.

Services:

- `postgres`: `postgres:16-alpine`, database/user/password all `noizy`, host port `5432` by default, volume `noizy-postgres-data`.
- `redis`: `redis:7-alpine`, host port `6379` by default.
- `localstack`: `localstack/localstack:3.8`, services `s3,sqs,sns`, host port `4566` by default, volume `noizy-localstack-data`.
- `localstack-init`: `amazon/aws-cli:2.17.42`, runs `docker/localstack/init-aws.sh`.
- `backend`: built from `./backend`, exposed on host port `8080` by default.
- `frontend`: built from `./frontend`, exposed on host port `8081` by default.

The backend waits for Postgres, Redis, and LocalStack init. Frontend waits for backend.

## LocalStack resources

The init script creates:

- S3 bucket `noizy-tracks`.
- S3 bucket `noizy-images`.
- SQS queue `noizy-track-events`.
- SNS topic `noizy-track-events`.
- SNS subscription to the SQS queue.
- Seed WAV objects under `seed/audio/`.
- Seed SVG covers under `seed/images/`.

Compose passes matching S3/SQS/SNS values to the backend.

## Environment files

`.env.example` documents local variables such as:

- `JWT_SECRET`
- `FRONTEND_ORIGIN`
- `AWS_ENDPOINT`
- `S3_TRACKS_BUCKET`
- `S3_IMAGES_BUCKET`

Do not commit real secrets.

## Terraform

Terraform root: `infra/terraform`.

Areas include:

- VPC/networking.
- EKS.
- ECR.
- RDS PostgreSQL.
- Redis/ElastiCache.
- S3 buckets.
- SQS/SNS.
- IAM/IRSA.
- Outputs.

Use `terraform fmt -check -recursive` for formatting checks. Full validation may require provider downloads.

## Kubernetes

Kubernetes manifests live in `infra/kubernetes`.

They cover namespace, config map, secret, service account, backend/frontend deployments, services, ingress, and HPAs. Placeholder image registry/account values must be replaced during deployment.

## Coupling to watch

- Bucket names must stay aligned across backend config, Docker Compose, LocalStack init, Terraform, and Kubernetes.
- JWT/frontend origin changes may touch backend config, Compose, Kubernetes config, frontend environments, and CORS.
- Streaming changes may touch backend Range headers, CORS exposed headers, frontend audio player, nginx proxy behavior, and validation.
