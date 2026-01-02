import json
import torch
import numpy as np
import boto3
import soundfile as sf
from util import extract_opera_feature


# ⚠️ load model OUTSIDE handler (cold start only once)
model = torch.jit.load("model.pt", map_location="cpu")
model.eval()

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

    features = extract_opera_feature(
        audio_file,
        pretrain="operaCT",
        input_sec=8,
        dim=dimension
    )

    features = np.array(event["features"], dtype=np.float32)
    x = torch.tensor(features).unsqueeze(0)

    with torch.no_grad():
        y = model(x)
        prob = torch.sigmoid(y).item()

    return {
        "statusCode": 200,
        "body": json.dumps({
            "probability": prob
        })
    }

def inference(file_ref: FileInS3):
    audio_file = np.array([file_path])

    dimension = 768

    features = extract_opera_feature(
        audio_file,
        pretrain="operaCT",
        input_sec=8,
        dim=dimension
    )

    x = np.array(features, dtype=np.float32)

    ckpt_path = "cks/linear/kauh/linear_operaCT768_32_0.0001_50_1e-05-epoch=39-valid_auc=0.83.ckpt"

    model = LinearHead(feat_dim=dimension, classes=2).load_from_checkpoint(
        ckpt_path,
        map_location=torch.device("cpu")
    )

    model.eval()

    with torch.no_grad():
        logits = model(torch.tensor(x))
        probs = torch.softmax(logits, dim=1)
        print("Prediction probabilities:", probs)
