import json
import os
import time
import logging
from typing import Any, Dict, Optional

import boto3
from botocore.config import Config
from botocore.exceptions import ClientError

from processor import process_message

logging.basicConfig(level=os.getenv("LOG_LEVEL", "INFO"))
log = logging.getLogger("opera-worker")

AWS_REGION = os.getenv("AWS_REGION")
QUEUE_URL = os.getenv("QUEUE_URL")

WAIT_TIME_SECONDS = int(os.getenv("WAIT_TIME_SECONDS", "20"))   # long polling
MAX_MESSAGES = int(os.getenv("MAX_MESSAGES", "1"))              # 1..10
VISIBILITY_TIMEOUT = int(os.getenv("VISIBILITY_TIMEOUT", "300"))# seconds

if not QUEUE_URL:
    raise RuntimeError("QUEUE_URL env var is required")

boto_cfg = Config(region_name=AWS_REGION, retries={"max_attempts": 10, "mode": "standard"})

ENDPOINT_URL = os.getenv("AWS_ENDPOINT_URL")
sqs = boto3.client("sqs", config=boto_cfg, endpoint_url=ENDPOINT_URL) if ENDPOINT_URL else boto3.client("sqs", config=boto_cfg)
s3  = boto3.client("s3",  config=boto_cfg, endpoint_url=ENDPOINT_URL) if ENDPOINT_URL else boto3.client("s3", config=boto_cfg)

def receive_one() -> Optional[Dict[str, Any]]:
    resp = sqs.receive_message(
        QueueUrl=QUEUE_URL,
        MaxNumberOfMessages=MAX_MESSAGES,
        WaitTimeSeconds=WAIT_TIME_SECONDS,
        VisibilityTimeout=VISIBILITY_TIMEOUT,
        MessageAttributeNames=["All"],
    )
    msgs = resp.get("Messages", [])
    return msgs[0] if msgs else None


def delete_message(receipt_handle: str) -> None:
    sqs.delete_message(QueueUrl=QUEUE_URL, ReceiptHandle=receipt_handle)


def change_visibility(receipt_handle: str, timeout: int) -> None:
    sqs.change_message_visibility(QueueUrl=QUEUE_URL, ReceiptHandle=receipt_handle, VisibilityTimeout=timeout)


def main_loop() -> None:
    log.info("Worker started. region=%s queue=%s", AWS_REGION, QUEUE_URL)

    while True:
        msg = receive_one()
        if not msg:
            continue

        receipt = msg["ReceiptHandle"]
        body = msg.get("Body", "")

        try:
            payload = json.loads(body)
        except Exception:
            log.exception("Bad JSON in message body: %s", body)
            # не видаляємо — хай піде в DLQ після maxReceiveCount
            continue

        start = time.time()
        try:
            log.info("Processing requestId=%s bucket=%s key=%s",
                     payload.get("requestId"), payload.get("bucket"), payload.get("key"))

            result = process_message(payload, s3)  # тут твоя логіка

            log.info("Processing result: %s", result)

            delete_message(receipt)
            log.info("Done in %.2fs requestId=%s", time.time() - start, payload.get("requestId"))

        except Exception as e:
            log.exception("Processing failed: %s", str(e))
            # опціонально: збільшити visibility, щоб дати більше часу на ретрай
            # change_visibility(receipt, 60)
            # НЕ delete -> SQS retry -> DLQ при потребі
            continue


if __name__ == "__main__":
    main_loop()