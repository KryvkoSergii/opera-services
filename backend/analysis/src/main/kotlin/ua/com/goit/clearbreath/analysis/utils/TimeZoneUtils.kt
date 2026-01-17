package ua.com.goit.clearbreath.analysis.utils

import java.time.ZoneId

object TimeZoneUtils {

    fun resolveZoneId(xTimezone: String?): ZoneId =
        try {
            if (xTimezone.isNullOrBlank()) ZoneId.of("UTC")
            else ZoneId.of(xTimezone)
        } catch (ex: Exception) {
            ZoneId.of("UTC")
        }
}
