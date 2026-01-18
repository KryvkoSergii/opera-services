#!/bin/sh
set -e

ENDPOINT=${AWS_ENDPOINT_URL}
REGION=${AWS_DEFAULT_REGION}
BUCKET=${FILE_STORAGE_BUCKET}
START_QUEUE_NAME=${INFERENCE_START_QUEUE_NAME}
RESULT_QUEUE_NAME=${INFERENCE_RESULT_QUEUE_NAME}

echo ENDPOINT="$ENDPOINT"
echo REGION="$REGION"
echo BUCKET="$BUCKET"
echo START_QUEUE_NAME="$START_QUEUE_NAME"
echo RESULT_QUEUE_NAME="$RESULT_QUEUE_NAME"

aws --region "$REGION" --endpoint-url="$ENDPOINT" s3 mb s3://"$BUCKET" 2>/dev/null || true
aws --region "$REGION" --endpoint-url="$ENDPOINT" sqs create-queue --queue-name "$START_QUEUE_NAME" >/dev/null
aws --region "$REGION" --endpoint-url="$ENDPOINT" sqs create-queue --queue-name "$RESULT_QUEUE_NAME" >/dev/null

echo "Infra initialized"