from models.models_eval import LinearHead
import numpy as np
import torch
from enums import AudioSource, AudioModality, DiseaseType
from classificators.classifier import Classifier

class LinearOperaClassifier(Classifier):
    def __init__(self, device: str, dimension: int, checkpoint_path: str, classes: int = 2):
        self.device = device
        self.model = self.load_model(checkpoint_path, device, dimension, classes)

    def load_model(self, checkpoint_path: str, device: str, dimension: int, classes: int):
        model = LinearHead.load_from_checkpoint(
            checkpoint_path,
            map_location=torch.device(self.device),
            feat_dim=dimension,
            classes=classes,
            from_feature=True
        )
        model.eval()
        return model

    def predict(self, features: np.ndarray):
        x = torch.tensor(features, device=self.device, dtype=torch.float32)
        with torch.no_grad():
            logits = self.model(x)
            probs = torch.softmax(logits, dim=1)
            return probs.numpy().squeeze()