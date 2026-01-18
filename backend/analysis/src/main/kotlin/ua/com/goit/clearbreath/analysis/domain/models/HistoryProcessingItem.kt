package ua.com.goit.clearbreath.analysis.domain.models

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("history_processing_item")
data class HistoryProcessingItem(
    @Id
    @Column("item_id")
    val itemId: UUID? = null,

    @Column("request_id")
    val requestId: UUID,

    @Column("file_location")
    val fileLocation: String? = null,

    @Column("processing_status")
    val processingStatus: ProcessingStatusEntity,
)
