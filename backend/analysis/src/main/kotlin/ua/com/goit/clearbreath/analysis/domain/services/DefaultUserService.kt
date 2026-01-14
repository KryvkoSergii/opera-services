package ua.com.goit.clearbreath.analysis.domain.services

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import ua.com.goit.clearbreath.analysis.domain.exceptions.UserExistsException
import ua.com.goit.clearbreath.analysis.domain.mapper.GenderMapper
import ua.com.goit.clearbreath.analysis.domain.models.UserEntity
import ua.com.goit.clearbreath.analysis.domain.repositories.UserRepository
import ua.com.goit.clearbreath.analysis.model.Gender
import ua.com.goit.clearbreath.analysis.utils.PasswordHasher

@Service
class DefaultUserService(
    private val userRepository: UserRepository,
    private val genderMapper: GenderMapper
) : UserService {

    override fun createUser(email: String, plainPassword: String, gender: Gender?): Mono<UserEntity> {
        return userRepository.findByEmail(email)
            .flatMap<UserEntity> {
                Mono.error(UserExistsException("User with email $email already exists"))
            }
            .switchIfEmpty(
                Mono.defer {
                    val userEntity = UserEntity(
                        passwordHash = PasswordHasher.hashPassword(plainPassword),
                        email = email,
                        gender = gender?.let { genderMapper.toEntity(it) }
                    )
                    userRepository.save(userEntity)
                }
            )
    }
}