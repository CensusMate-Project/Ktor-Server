package org.censusmate.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class HouseholdResponseDto(
    val id: String,
    val enumeratorId: String?,
    val eventId: String?,
    val address: String,
    val totalResidents: Int,
    val dwellingType: String?,
    val buildingYear: String?,
    val totalArea: Int?,
    val livingArea: Int?,
    val roomsCount: Int?,
    val notes: String?,
    val createdAt: String,
    val updatedAt: String?
)

@Serializable
data class CreateHouseholdRequestDto(
    val address: String,
    val totalResidents: Int,
    val dwellingType: String? = null,
    val buildingYear: String? = null,
    val totalArea: Int? = null,
    val livingArea: Int? = null,
    val roomsCount: Int? = null,
    val notes: String? = null
)

@Serializable
data class UpdateHouseholdRequestDto(
    val address: String? = null,
    val totalResidents: Int? = null,
    val dwellingType: String? = null,
    val buildingYear: String? = null,
    val totalArea: Int? = null,
    val livingArea: Int? = null,
    val roomsCount: Int? = null,
    val notes: String? = null
)