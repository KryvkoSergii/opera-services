from feature_extraction import extract_features
from classifier import apply_classifier_t5
from util import FileInS3
import numpy as np
import torch

device: str = "gpu" if torch.cuda.is_available() else "cpu"

def process_message(payload, s3_client):
    requestId = payload.get("requestId")
    bucket = payload.get("bucket")
    key = payload.get("key")

    features = extract_features(file_ref=FileInS3(s3_client, bucket, key),
                                device=device,
                                model_name="operaCT",
                                input_sec=8,
                                dim=768)
    x = np.array(features, dtype=np.float32)

    return apply_classifier_t5(x, 768, device)
