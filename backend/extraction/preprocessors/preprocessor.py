from abc import ABC, abstractmethod
from util import FileInS3

class PreProcessor(ABC):
    def __init__(self, device: str):
        self.device = device

    @abstractmethod
    def process(self, file_ref: FileInS3) -> any:
        pass

    @abstractmethod
    def preprocessor_name(self) -> str:
        pass