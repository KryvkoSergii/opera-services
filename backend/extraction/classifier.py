from models.models_eval import LinearHead
import numpy as np
import torch

def apply_classifier_t5(features: np.ndarray, dimension: int, device: str) -> np.ndarray:
    ckpt_path = "libs/linear_covid_operaCT768_valid_auc=0.55.ckpt"
    # model = LinearHead(feat_dim=dimension, classes=2).load_from_checkpoint(
    #     ckpt_path,
    #     map_location=torch.device(device)
    # )

    model = LinearHead.load_from_checkpoint(
        ckpt_path,
        map_location=torch.device(device),
        feat_dim=dimension,
        classes=2,
        from_feature=True
    )

    model.eval()

    with torch.no_grad():
        logits = model(torch.tensor(features))
        probs = torch.softmax(logits, dim=1)
        return probs.numpy()