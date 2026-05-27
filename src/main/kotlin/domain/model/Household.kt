package org.censusmate.domain.model

import java.time.LocalDateTime
import java.util.UUID

data class Household(
    val id: UUID,
    val enumeratorId: UUID?,
    val eventId: UUID?,
    val address: String,
    val totalResidents: Int,
    val dwellingType: String?,
    val buildingYear: String?,
    val totalArea: Int?,
    val livingArea: Int?,
    val roomsCount: Int?,
    val notes: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)