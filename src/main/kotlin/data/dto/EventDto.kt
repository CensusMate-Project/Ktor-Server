package org.censusmate.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class EventResponseDto(
    val id: String,
    val name: String,
    val startDatetime: String,
    val endDatetime: String,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String?
)

@Serializable
data class EventsResponseDto(
    val events: List<EventResponseDto>,
    val pagination: PaginationResponseDto
)

@Serializable
data class CreateEventRequestDto(
    val name: String,
    val startDatetime: String,
    val endDatetime: String
)

@Serializable
data class UpdateEventRequestDto(
    val name: String? = null,
    val startDatetime: String? = null,
    val endDatetime: String? = null
)