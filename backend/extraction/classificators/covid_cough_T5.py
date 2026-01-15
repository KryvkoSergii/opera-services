from enums import AudioSource, AudioModality, DiseaseType
from classificators.linear_opera_classifier import LinearOperaClassifier

checkpoint_path = "libs/linear_coviduk_cough_operaCT768_valid_auc=0.74.ckpt"

class CovidCough2(LinearOperaClassifier):
    def __init__(self, device: str):
        super().__init__(device, 768, checkpoint_path)

    def source(self) -> AudioSource:
        return AudioSource.MICROPHONE

    def modality(self) -> AudioModality:
        return AudioModality.COUGH

    def diagnose(self) -> DiseaseType:
        return DiseaseType.COVID19

    def preprocessor_name(self) -> str:
        return "operaCT_768"

    def task(self) -> str:
        return "COVIDUK_COUGH_OperaCT_768"

    def requires_preprocessor(self) -> bool:
        return True