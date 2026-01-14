package ua.com.goit.clearbreath.analysis.domain.models

enum class ProcessingStatusEntity(val dbValue: String) {
    NEW("NEW"),
    UPLOADING("UPLOADING"),
    PROCESSING("PROCESSING"),
    DONE("DONE"),
    FAILED("FAILED");
}