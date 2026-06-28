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

printf 'RIFF\344\135\000\000WAVEfmt \020\000\000\000\001\000\001\000\100\037\000\000\100\037\000\000\001\000\010\000data\300\135\000\000' > /tmp/noizy-sample.wav
dd if=/dev/zero bs=24000 count=1 >> /tmp/noizy-sample.wav 2>/dev/null
for key in dawn-circuit signal-bloom window-notes platform-six; do
  aws --endpoint-url="$endpoint" s3 cp /tmp/noizy-sample.wav "s3://noizy-tracks/seed/audio/$key.wav" --content-type audio/wav >/dev/null
done

write_cover() {
  title="$1"
  color_a="$2"
  color_b="$3"
  file="$4"
  cat > "$file" <<EOF
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512">
  <defs>
    <linearGradient id="g" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0" stop-color="$color_a"/>
      <stop offset="1" stop-color="$color_b"/>
    </linearGradient>
  </defs>
  <rect width="512" height="512" rx="46" fill="url(#g)"/>
  <circle cx="154" cy="166" r="54" fill="#111111" opacity=".18"/>
  <path d="M168 340c88-106 140-126 246-130v132c-82-10-142 0-224 48-38 22-72-14-22-50z" fill="#111111" opacity=".28"/>
  <path d="M102 326h236" stroke="#ffffff" stroke-width="22" stroke-linecap="round" opacity=".82"/>
  <path d="M102 382h164" stroke="#ffffff" stroke-width="22" stroke-linecap="round" opacity=".62"/>
  <text x="64" y="112" fill="#111111" font-family="Arial, sans-serif" font-size="34" font-weight="900" opacity=".78">$title</text>
</svg>
EOF
}

write_cover "Golden Hour" "#facc15" "#38bdf8" /tmp/golden-hour-signals.svg
write_cover "Sunlight" "#f97316" "#ec4899" /tmp/rooms-with-sunlight.svg
write_cover "Platform" "#22c55e" "#2563eb" /tmp/late-platform.svg
write_cover "Solar Drift" "#facc15" "#22c55e" /tmp/solar-drift.svg
write_cover "Marta Vale" "#ec4899" "#38bdf8" /tmp/marta-vale.svg
write_cover "Northline" "#a78bfa" "#facc15" /tmp/northline-trio.svg

for key in golden-hour-signals rooms-with-sunlight late-platform solar-drift marta-vale northline-trio; do
  aws --endpoint-url="$endpoint" s3 cp "/tmp/$key.svg" "s3://noizy-images/seed/images/$key.svg" --content-type image/svg+xml >/dev/null
done

echo "LocalStack resources ready in $region"
