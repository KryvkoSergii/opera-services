package ua.com.goit.clearbreath.analysis.domain.services

import reactor.core.publisher.Mono
import ua.com.goit.clearbreath.analysis.domain.models.UserEntity
import ua.com.goit.clearbreath.analysis.model.Gender

interface UserService {
    fun createUser(email: String, plainPassword: String, gender: Gender?): Mono<UserEntity>
}