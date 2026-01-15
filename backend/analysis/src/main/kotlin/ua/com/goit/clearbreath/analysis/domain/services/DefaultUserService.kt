package ua.com.goit.clearbreath.analysis.domain.services

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import ua.com.goit.clearbreath.analysis.domain.exceptions.UserExistsException
import ua.com.goit.clearbreath.analysis.domain.exceptions.UserNotFoundException
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

    override suspend fun createUser(email: String, plainPassword: String, gender: Gender?): UserEntity {
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
            ).awaitSingle()
    }

    override suspend fun findUserByEmail(email: String): UserEntity {
        return userRepository.findByEmail(email)
            .flatMap { user ->
                if (user != null) {
                    Mono.just(user)
                } else {
                    Mono.error(UserNotFoundException("User $email does not exist"))
                }
            }.awaitSingle()
    }

    override suspend fun getCurrentUser(): UserEntity {
        return ReactiveSecurityContextHolder.getContext()
            .map { it.authentication.principal as UserEntity }
            .awaitSingle()
    }


}