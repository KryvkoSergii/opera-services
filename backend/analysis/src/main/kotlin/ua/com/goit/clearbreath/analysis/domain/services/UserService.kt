package ua.com.goit.clearbreath.analysis.domain.services

import ua.com.goit.clearbreath.analysis.domain.models.UserEntity
import ua.com.goit.clearbreath.analysis.model.Gender

interface UserService {
    suspend fun createUser(email: String, plainPassword: String, gender: Gender?): UserEntity
    suspend fun findUserByEmail(email: String): UserEntity
}