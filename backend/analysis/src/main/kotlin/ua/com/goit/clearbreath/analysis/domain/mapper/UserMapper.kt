package ua.com.goit.clearbreath.analysis.domain.mapper

import org.mapstruct.Mapper
import org.springframework.stereotype.Component
import ua.com.goit.clearbreath.analysis.domain.models.UserEntity
import ua.com.goit.clearbreath.analysis.model.UserRegisterResponse

@Component
@Mapper(componentModel = "spring")
interface UserMapper {
    fun toRegResponse(user: UserEntity): UserRegisterResponse
}