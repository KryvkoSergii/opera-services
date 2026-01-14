package ua.com.goit.clearbreath.analysis.domain.mapper

import org.mapstruct.Mapper
import org.springframework.stereotype.Component
import ua.com.goit.clearbreath.analysis.domain.models.GenderEntity
import ua.com.goit.clearbreath.analysis.model.Gender

@Component
class GenderMapper {

    fun toDto(gender: GenderEntity?): Gender? =
        gender?.name?.let { Gender.valueOf(it) }

    fun toEntity(gender: Gender?): GenderEntity? =
        gender?.name?.let { GenderEntity.valueOf(it) }
}