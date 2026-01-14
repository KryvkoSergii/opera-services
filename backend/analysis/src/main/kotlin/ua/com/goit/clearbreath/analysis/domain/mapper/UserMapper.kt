package ua.com.goit.clearbreath.analysis.domain.mapper

import org.springframework.stereotype.Component
import ua.com.goit.clearbreath.analysis.domain.models.UserEntity
import ua.com.goit.clearbreath.analysis.model.UserRegisterResponse
import java.time.ZoneId

@Component
class UserMapper(private val genderMapper: GenderMapper) {
    fun toRegResponse(user: UserEntity): UserRegisterResponse{
        return UserRegisterResponse(
            userId = user.userId,
            email = user.email,
            gender = genderMapper.toDto(user.gender),
            registeredAt = user.registeredAt?.atZone(ZoneId.systemDefault())?.toOffsetDateTime()
        )
    }
}