package ua.com.goit.clearbreath.analysis.tasks

import java.nio.file.Path
import java.util.UUID

data class ConvertFileEvent (val requestId: UUID, val onLocalDisk: Path)