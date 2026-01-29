package ua.com.goit.clearbreath.analysis.domain.models

enum class ProcessingStatusEntity(val dbValue: String) {
    NEW("NEW"),
    UPLOADING("UPLOADING"),
    UPLOADED("UPLOADED"),
    PROCESSING("PROCESSING"),
    PARTIAL_DONE("PARTIAL_DONE"),
    DONE("DONE"),
    FAILED("FAILED");
}