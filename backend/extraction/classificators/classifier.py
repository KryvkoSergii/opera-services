from models.models_eval import LinearHead
import numpy as np
import torch
from enums import AudioSource, AudioModality, DiseaseType
from abc import ABC, abstractmethod

class Classifier(ABC):
    def __init__(self, device: str, dimension: int, checkpoint_path: str, classes: int = 2):
        self.device = device
        self.model = self.load_model(checkpoint_path, device, dimension, classes)

    @abstractmethod
    def load_model(self, checkpoint_path: str, device: str, dimension: int, classes: int):
        pass

    @abstractmethod
    def predict(self, features: np.ndarray):
        pass

    def metadata(self):
        return {
            "source": self.source(),
            "modality": self.modality(),
            "diagnose": self.diagnose(),
            "model": self.preprocessor_name(),
            "task": self.task()
        }

    @abstractmethod
    def source(self) -> AudioSource:
        pass
    @abstractmethod
    def modality(self) -> AudioModality:
        pass

    @abstractmethod
    def diagnose(self) -> DiseaseType:
        pass

    @abstractmethod
    def preprocessor_name(self) -> str:
        pass

    @abstractmethod
    def task(self) -> str:
        pass

    @abstractmethod
    def requires_preprocessor(self) -> bool:
        pass

    @abstractmethod
    def positive_probability(self, probs) -> float:
        pass


