package ua.com.goit.clearbreath.analysis.domain.repositories

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import ua.com.goit.clearbreath.analysis.domain.models.UserEntity
import java.util.*

@Repository
interface UserRepository: ReactiveCrudRepository<UserEntity, UUID> {
    fun findByEmail(email: String): Mono<UserEntity?>
}