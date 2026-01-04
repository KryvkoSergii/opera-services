# import json
# import torch
# import numpy as np
# import boto3
# import soundfile as sf
from util import extract_opera_feature, FileInS3


# ⚠️ load model OUTSIDE handler (cold start only once)
# model = torch.jit.load("model.pt", map_location="cpu")
# model.eval()

def extract_features(file_ref: FileInS3, device: str, model_name: str, input_sec: int, dim: int):
    if model_name == "operaCT":
        features = extract_opera_feature(
            file_ref=file_ref,
            device=device,
            pretrain="operaCT",
            input_sec=8,
            dim=768
        )
        return features
    else:
        raise ValueError(f"Unsupported model: {model_name}")

# def handler(event, context):
#     """
#     event example:
#     {
#       "features": [0.1, 0.2, ...]
#     }
#     """
#
#     bucket = event['bucket']
#     key = event['key']
#     model = event['model'] # e.g., operaCT
#     file_ref = FileInS3(bucket, key)
#
#     dimension = 768
#     input_sec = 8
#
#     features = extract_opera_feature(
#         file_ref = file_ref,
#         pretrain="operaCT",
#         input_sec=input_sec,
#         dim=dimension
#     )
#
#     x = np.array(features, dtype=np.float32)
#
#     prob = apply_classifier(x, dimension)
#
#     return {
#         "statusCode": 200,
#         "body": json.dumps({
#             "probability": prob
#         })
#     }
