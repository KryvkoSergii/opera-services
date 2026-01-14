package ua.com.goit.clearbreath.analysis.domain.mapper

import org.mapstruct.Mapper
import org.springframework.stereotype.Component
import ua.com.goit.clearbreath.analysis.domain.models.SourceTypeEntity
import ua.com.goit.clearbreath.analysis.model.SourceType

@Component
@Mapper(componentModel = "spring")
interface SourceTypeMapper {
    fun toEntity(type: SourceType): SourceTypeEntity
    fun toDto(type: SourceTypeEntity): SourceType
}