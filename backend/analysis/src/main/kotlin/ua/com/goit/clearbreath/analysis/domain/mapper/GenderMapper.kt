package ua.com.goit.clearbreath.analysis.domain.mapper

import org.mapstruct.Mapper
import org.springframework.stereotype.Component
import ua.com.goit.clearbreath.analysis.domain.models.GenderEntity
import ua.com.goit.clearbreath.analysis.model.Gender

@Component
@Mapper(componentModel = "spring")
interface GenderMapper {
    fun toDto(gender: GenderEntity): Gender = Gender.forValue(gender.name)
    fun toEntity(gender: Gender): GenderEntity = GenderEntity.(gender.name)
}