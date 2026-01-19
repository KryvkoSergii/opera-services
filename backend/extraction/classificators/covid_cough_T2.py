from enums import AudioSource, AudioModality, DiseaseType
from classificators.linear_opera_classifier import LinearOperaClassifier

path = "libs/linear_covid_cough_operaCT768_valid_auc=0.55.ckpt"

class CovidCough1(LinearOperaClassifier):
    def __init__(self, device: str):
        super().__init__(device, 768, path)

    def source(self) -> AudioSource:
        return AudioSource.MICROPHONE

    def modality(self) -> AudioModality:
        return AudioModality.COUGH

    def diagnose(self) -> DiseaseType:
        return DiseaseType.COVID19

    def preprocessor_name(self) -> str:
        return "operaCT_768"

    def task(self) -> str:
        return "COVID_COUGH_OperaCT_768"

    def requires_preprocessor(self) -> bool:
        return True

    def positive_probability(self, probs) -> float:
        # "COVID-19": 1,
        # "healthy": 0,
        return float(probs[1])

