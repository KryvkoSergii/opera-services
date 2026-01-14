package ua.com.goit.clearbreath.analysis.domain.mapper

import org.springframework.stereotype.Component
import ua.com.goit.clearbreath.analysis.domain.models.ProcessingStatusEntity
import ua.com.goit.clearbreath.analysis.model.RequestStatus

@Component
class ProcessingStatusMapper {
    fun toEntity(status: RequestStatus): ProcessingStatusEntity = ProcessingStatusEntity.valueOf(status.name)
    fun toDto(status: ProcessingStatusEntity): RequestStatus = RequestStatus.valueOf(status.name)
}