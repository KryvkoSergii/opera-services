package ua.com.goit.clearbreath.analysis.domain.models

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("history_item_result")
data class HistoryItemResult(
    @Id
    @Column("result_id")
    val resultId: UUID? = null,

    @Column("item_id")
    val itemId: UUID,

    @Column("request_id")
    val requestId: UUID,

    @Column("file_location")
    val fileLocation: String?,

    @Column("model_name")
    val modelName: String,

    @Column("diagnose")
    val diagnose: String,

    @Column("probability")
    val probability: Double,
)
