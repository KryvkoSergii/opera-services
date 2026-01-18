from util import FileInS3
import numpy as np
import os
import torch
from enums import AudioSource
from classificators.classifier import Classifier
from classificators.covid_cough_T2 import CovidCough1
from classificators.covid_cough_T5 import CovidCough2
from classificators.copd_lung_T7 import CopdLung
from preprocessors.preprocessor import PreProcessor
from preprocessors.operaCT_768_preprocessor import OperaCT768PreProcessor
from typing import List, Dict
from contracts.models.inference_start_event_payload import InferenceStartEventPayload
from contracts.models.event_source import EventSource

device: str = "gpu" if torch.cuda.is_available() else "cpu"
bucket: str = os.getenv("FILE_STORAGE_BUCKET")

available_classifiers: List[Classifier] = [CovidCough1(device), CovidCough2(device), CopdLung(device)]
available_preprocessors: Dict[str, PreProcessor] = {
    "operaCT_768": OperaCT768PreProcessor(device)
}

class ResultItem:
    def __init__(self, task: str, probability: float):
        self.task = task
        self.probability = probability

def process_message(payload: InferenceStartEventPayload, s3_client) -> List[ResultItem]:
    requestId = payload.request_id
    itemId = payload.item_id
    key = payload.file_location
    source = payload.source  # microphone, stethoscope

    request_source = parse_audio_source(source)

    matching_classifiers: list[Classifier] = [
        cls
        for cls in available_classifiers
        if cls.source() == request_source
    ]

    cache: Dict[str, np.ndarray] = {}
    result: List[ResultItem] = []

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
        result_item = ResultItem(classifier.task(), prob)
        result.append(result_item)

    return result

def parse_audio_source(source: EventSource) -> AudioSource:
    if not source:
        raise ValueError("source is required")

    try:
        return AudioSource(source.value)
    except ValueError:
        raise ValueError(f"Unknown audio source: {value}")
