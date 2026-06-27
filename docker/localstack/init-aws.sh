#!/bin/sh
set -eu

endpoint="${AWS_ENDPOINT:-http://localstack:4566}"
region="${AWS_DEFAULT_REGION:-us-east-1}"

until aws --endpoint-url="$endpoint" s3 ls >/dev/null 2>&1; do
  sleep 2
done

aws --endpoint-url="$endpoint" s3 mb s3://noizy-tracks 2>/dev/null || true
aws --endpoint-url="$endpoint" s3 mb s3://noizy-images 2>/dev/null || true

aws --endpoint-url="$endpoint" sqs create-queue --queue-name noizy-track-events >/dev/null
topic_arn="$(aws --endpoint-url="$endpoint" sns create-topic --name noizy-track-events --query TopicArn --output text)"
queue_url="$(aws --endpoint-url="$endpoint" sqs get-queue-url --queue-name noizy-track-events --query QueueUrl --output text)"
queue_arn="$(aws --endpoint-url="$endpoint" sqs get-queue-attributes --queue-url "$queue_url" --attribute-names QueueArn --query 'Attributes.QueueArn' --output text)"
aws --endpoint-url="$endpoint" sns subscribe --topic-arn "$topic_arn" --protocol sqs --notification-endpoint "$queue_arn" >/dev/null

printf 'ID3%096d' 0 > /tmp/noizy-sample.mp3
for key in dawn-circuit signal-bloom window-notes platform-six; do
  aws --endpoint-url="$endpoint" s3 cp /tmp/noizy-sample.mp3 "s3://noizy-tracks/seed/audio/$key.mp3" >/dev/null
done

echo "LocalStack resources ready in $region"
