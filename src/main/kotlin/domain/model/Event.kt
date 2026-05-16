package org.censusmate.domain.model

import java.time.LocalDateTime
import java.util.UUID

data class Event(
    val id: UUID,
    val name: String,
    val startDatetime: LocalDateTime,
    val endDatetime: LocalDateTime,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
) {
    val isActive: Boolean
        get() = LocalDateTime.now().let { now ->
            now.isAfter(startDatetime) && now.isBefore(endDatetime)
        }
}