#!/bin/sh
set -e

ENDPOINT="http://localstack:4566"
REGION="${AWS_DEFAULT_REGION:-eu-central-1}"

aws --region "$REGION" --endpoint-url="$ENDPOINT" s3 mb s3://input-bucket 2>/dev/null || true
aws --region "$REGION" --endpoint-url="$ENDPOINT" s3 mb s3://results-bucket 2>/dev/null || true

aws --region "$REGION" --endpoint-url="$ENDPOINT" sqs create-queue --queue-name opera-queue >/dev/null

aws --region "$REGION" --endpoint-url="$ENDPOINT" s3 cp /samples/breath.wav s3://input-bucket/audio/breath.wav

echo "Infra initialized"

aws --region "$REGION" --endpoint-url="$ENDPOINT" sqs send-message \
  --queue-url http://localhost:4566/000000000000/opera-queue \
  --message-body '{"bucket":"input-bucket","key":"audio/breath.wav","requestId":"9aa0d17b-a638-4002-ac3c-7b4259b82b1f","pretrain":"operaCT","inputSec":8,"fromSpec":false}'

echo "Event initialized"