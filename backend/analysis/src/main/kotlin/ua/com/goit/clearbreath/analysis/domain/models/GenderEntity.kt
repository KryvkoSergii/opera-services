package ua.com.goit.clearbreath.analysis.domain.models

import com.fasterxml.jackson.annotation.JsonCreator
import ua.com.goit.clearbreath.analysis.model.Gender

enum class GenderEntity(val dbValue: String) {
    MALE("male"),
    FEMALE("female")

}
