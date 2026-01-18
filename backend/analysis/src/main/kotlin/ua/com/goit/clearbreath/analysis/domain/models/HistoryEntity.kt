package ua.com.goit.clearbreath.analysis.domain.models

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.*

@Table("history")
data class HistoryEntity(

    @Id
    @Column("request_id")
    val requestId: UUID? = null,

    @Column("processing_status")
    val processingStatus: ProcessingStatusEntity,

    @Column("source_type")
    val sourceType: SourceTypeEntity? = null,

    @Column("user_id")
    val user: UUID? = null,

    @Column("recommendation")
    val recommendation: String? = null,

    @CreatedDate
    @Column("created_at")
    val createdAt: LocalDateTime? = null,

    @LastModifiedDate
    @Column("updated_at")
    val updatedAt: LocalDateTime? = null,
)