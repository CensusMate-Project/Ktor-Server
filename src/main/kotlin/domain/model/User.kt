package org.censusmate.domain.model

import java.time.LocalDateTime
import java.util.UUID

enum class RoleType {
    AGENT,
    ADMINISTRATOR;

    fun toDbValue() = name.lowercase()

    companion object {
        fun fromDbValue(value: String): RoleType =
            entries.find { it.name.equals(value, ignoreCase = true) }
                ?: throw IllegalArgumentException("Unknown role: $value")
    }
}

data class User(
    val id: UUID,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: RoleType,
    val isBlocked: Boolean,
    val defaultUser: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
) {
    val fullName: String get() = "$firstName $lastName".trim()
}