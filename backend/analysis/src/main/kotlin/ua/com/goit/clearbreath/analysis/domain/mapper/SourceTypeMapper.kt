package ua.com.goit.clearbreath.analysis.domain.mapper

import org.springframework.stereotype.Component
import ua.com.goit.clearbreath.analysis.domain.models.SourceTypeEntity
import ua.com.goit.clearbreath.analysis.events.EventSource
import ua.com.goit.clearbreath.analysis.model.SourceType

@Component
class SourceTypeMapper {
    fun toEntity(type: SourceType): SourceTypeEntity = SourceTypeEntity.valueOf(type.name)
    fun toDto(type: SourceTypeEntity): SourceType = SourceType.valueOf(type.name)
    fun toEvent(type: SourceTypeEntity): EventSource = EventSource.valueOf(type.name)
}