from util import FileInS3
import numpy as np
import torch
from enums import AudioSource
from classificators.classifier import Classifier
from classificators.covid_cough_T2 import CovidCough1
from classificators.covid_cough_T5 import CovidCough2
from preprocessors.preprocessor import PreProcessor
from preprocessors.operaCT_768_preprocessor import OperaCT768PreProcessor
from typing import List, Dict

device: str = "gpu" if torch.cuda.is_available() else "cpu"

available_classifiers: List[Classifier] = [CovidCough1(device), CovidCough2(device)]
available_preprocessors: Dict[str, PreProcessor] = {
    "operaCT_768": OperaCT768PreProcessor(device)
}

def process_message(payload, s3_client):
    requestId = payload.get("requestId")
    bucket = payload.get("bucket")
    key = payload.get("key")
    # modality = payload.get("modality")  # cough, breath, lung_sounds
    source = payload.get("source")  # microphone, stethoscope

    request_source = parse_audio_source(source)

    matching_classifiers: list[Classifier] = [
        cls
        for cls in available_classifiers
        if cls.source() == request_source
    ]

    cache: Dict[str, np.ndarray] = {}
    result: Dict[str, Any] = {}

    for classifier in matching_classifiers:

        if classifier.requires_preprocessor():
            preprocessor_name = classifier.preprocessor_name()

            if cache.get(preprocessor_name) is not None:
                features = cache.get(preprocessor_name)
            else:
                preprocessor = available_preprocessors.get(classifier.preprocessor_name())

                if preprocessor is None:
                    raise ValueError(
                        f"Preprocessor {classifier.preprocessor_name()} not found")

                features = preprocessor.process(FileInS3(s3_client, bucket, key))
                cache[preprocessor_name] = features

        prob = classifier.predict(features)
        result[classifier.task()] = prob

    return result

def parse_audio_source(value: str) -> AudioSource:
    if not value:
        raise ValueError("source is required")

    value = value.lower()

    try:
        return AudioSource(value)
    except ValueError:
        raise ValueError(f"Unknown audio source: {value}")
