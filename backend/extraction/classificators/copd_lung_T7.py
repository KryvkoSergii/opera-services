from enums import AudioSource, AudioModality, DiseaseType
from classificators.linear_opera_classifier import LinearOperaClassifier

path = "libs/linear_icbhidisease_operaCT768_valid_auc=0.99.ckpt"

class CopdLung(LinearOperaClassifier):
    def __init__(self, device: str):
        super().__init__(device, 768, path)

    def source(self) -> AudioSource:
        return AudioSource.STETHOSCOPE

    def modality(self) -> AudioModality:
        return AudioModality.LUNG_SOUNDS

    def diagnose(self) -> DiseaseType:
        return DiseaseType.COPD

    def preprocessor_name(self) -> str:
        return "operaCT_768"

    def task(self) -> str:
        return "ICBHI_DISEASE_OperaCT_768"

    def requires_preprocessor(self) -> bool:
        return True