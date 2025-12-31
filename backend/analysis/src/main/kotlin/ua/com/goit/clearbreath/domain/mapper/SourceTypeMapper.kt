package ua.com.goit.clearbreath.domain.mapper

import org.mapstruct.Mapper
import org.springframework.stereotype.Component
import ua.com.goit.clearbreath.domain.models.SourceTypeEntity
import ua.com.goit.clearbreath.model.RequestStatus
import ua.com.goit.clearbreath.model.SourceType

@Component
@Mapper(componentModel = "spring")
interface SourceTypeMapper {
    fun toEntity(type: SourceType): SourceTypeEntity
    fun toDto(type: SourceTypeEntity): SourceType
}