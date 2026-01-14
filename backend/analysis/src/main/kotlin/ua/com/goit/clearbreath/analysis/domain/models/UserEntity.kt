package ua.com.goit.clearbreath.analysis.domain.models

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.UUID

@Table("users")
data class UserEntity(
    @Id
    @Column("user_id")
    val userId: UUID? = null,

    @Column("password_hash")
    val passwordHash: String,

    @Column("email")
    val email: String,

    @Column("gender")
    val gender: GenderEntity? = null,

    @CreatedDate
    @Column("registered_at")
    val registeredAt: LocalDateTime? = null
)