from enum import Enum

class AudioSource(Enum):
    MICROPHONE = "microphone"
    STETHOSCOPE = "stethoscope"

class AudioModality(Enum):
    COUGH = "cough"
    BREATH = "breath"
    LUNG_SOUNDS = "lung_sounds"

class DiseaseType(Enum):
    COVID19 = "COVID-19"
    COPD = "COPD" # Chronic Obstructive Pulmonary Disease
