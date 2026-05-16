package org.censusmate.data.dto

import kotlinx.serialization.SerialName
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
data class CreateEventRequestDto(
    val name: String,
    @SerialName("start_datetime")
    val startDatetime: String,
    @SerialName("end_datetime")
    val endDatetime: String
)

@Serializable
data class UpdateEventRequestDto(
    val name: String? = null,
    @SerialName("start_datetime")
    val startDatetime: String? = null,
    @SerialName("end_datetime")
    val endDatetime: String? = null
)