package ua.com.goit.clearbreath.analysis.domain.mapper

import org.mapstruct.Mapper
import org.springframework.stereotype.Component
import ua.com.goit.clearbreath.analysis.domain.models.ProcessingStatusEntity
import ua.com.goit.clearbreath.analysis.model.RequestStatus

@Component
@Mapper(componentModel = "spring")
interface ProcessingStatusMapper {
    fun toEntity(status: RequestStatus): ProcessingStatusEntity
    fun toDto(status: ProcessingStatusEntity): RequestStatus
}