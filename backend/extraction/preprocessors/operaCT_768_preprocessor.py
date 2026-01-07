from preprocessors.preprocessor import PreProcessor
from util import extract_opera_feature, FileInS3
import numpy as np

class OperaCT768PreProcessor(PreProcessor):
    def __init__(self, device):
        super().__init__(device)

    def process(self, file_ref: FileInS3) -> np.ndarray:
        features = extract_opera_feature(
            file_ref=file_ref,
            device=self.device,
            pretrain="operaCT",
            input_sec=2,
            dim=768
        )
        x = np.array(features, dtype=np.float32)
        return x

    def preprocessor_name(self) -> str:
        return "operaCT_768"