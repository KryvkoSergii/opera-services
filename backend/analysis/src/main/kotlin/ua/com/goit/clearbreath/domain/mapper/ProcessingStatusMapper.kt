package ua.com.goit.clearbreath.domain.mapper

import org.mapstruct.Mapper
import org.springframework.stereotype.Component
import ua.com.goit.clearbreath.domain.models.ProcessingStatusEntity
import ua.com.goit.clearbreath.model.RequestStatus

@Component
@Mapper(componentModel = "spring")
interface ProcessingStatusMapper {
    fun toEntity(status: RequestStatus): ProcessingStatusEntity
    fun toDto(status: ProcessingStatusEntity): RequestStatus
}