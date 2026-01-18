package ua.com.goit.clearbreath.analysis.tasks

import ua.com.goit.clearbreath.analysis.domain.models.SourceTypeEntity
import java.nio.file.Path
import java.util.UUID

data class ConvertFileEvent (val requestId: UUID, val onLocalDisk: Path, val source: SourceTypeEntity)