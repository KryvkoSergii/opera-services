import json
import torch
import numpy as np
import boto3
import soundfile as sf
from util import extract_opera_feature
from models.models_eval import LinearHead


# ⚠️ load model OUTSIDE handler (cold start only once)
# model = torch.jit.load("model.pt", map_location="cpu")
# model.eval()

def handler(event, context):
    """
    event example:
    {
      "features": [0.1, 0.2, ...]
    }
    """

    bucket = event['bucket']
    key = event['key']
    model = event['model'] # e.g., operaCT
    file_ref = FileInS3(bucket, key)

    dimension = 768
    input_sec = 8

    features = extract_opera_feature(
        file_ref = file_ref,
        pretrain="operaCT",
        input_sec=input_sec,
        dim=dimension
    )

    x = np.array(features, dtype=np.float32)

    prob = apply_classifier(x, dimension)

    return {
        "statusCode": 200,
        "body": json.dumps({
            "probability": prob
        })
    }

def apply_classifier(x: np.ndarray, dimension: int):
    ckpt_path = "libs/linear_covid_operaCT768_valid_auc=0.55.ckpt"
    model = LinearHead(feat_dim=dimension, classes=2).load_from_checkpoint(
        ckpt_path,
        map_location=torch.device("cpu")
    )

    model.eval()

    with torch.no_grad():
        logits = model(torch.tensor(x))
        probs = torch.softmax(logits, dim=1)
        return probs.numpy()
