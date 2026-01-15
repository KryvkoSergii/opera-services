package ua.com.goit.clearbreath.analysis.domain.models

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("evaluation_model_metadata")
data class EvaluationModelMetadataEntity(
    @Id
    @Column("model_id")
    val userId: UUID,

    @Column("model_name")
    val modelName: String,

    @Column("diagnose")
    val diagnose: String,

    @Column("experimental_relevance")
    val experimentalRelevance: Double,

    @Column("description")
    val description: String
)