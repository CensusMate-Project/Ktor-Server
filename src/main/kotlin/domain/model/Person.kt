package org.censusmate.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

enum class GenderType {
    MALE, FEMALE;

    fun toDbValue() = name.lowercase()

    companion object {
        fun fromDbValue(value: String?): GenderType? {
            if (value == null) return null
            return entries.find { it.name.equals(value, ignoreCase = true) }
                ?: throw IllegalArgumentException("Unknown gender: $value")
        }
    }
}

data class Person(
    val id: UUID,
    val householdId: UUID,
    val gender: GenderType?,
    val birthDate: LocalDate,
    val citizenship: String?,
    val hasDualCitizenship: Boolean?,
    val nationality: String?,
    val nativeLanguage: String?,
    val speaksRussian: Boolean?,
    val otherLanguages: List<String>,
    val educationLevel: String?,
    val maritalStatus: String?,
    val childrenCount: Int?,
    val relationToHousehold: String?,
    val placeOfBirth: String?,
    val currentResidence: String?,
    val incomeSources: List<String>,
    val employmentStatus: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)