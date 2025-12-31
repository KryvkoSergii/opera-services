package ua.com.goit.clearbreath.domain.models

enum class ProcessingStatusEntity(val dbValue: String) {
    IN_PROGRESS("IN_PROGRESS"),
    FINISHED("FINISHED"),
    FAILED("FAILED");
}