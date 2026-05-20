package org.censusmate.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PersonResponseDto(
    val id: String,
    val householdId: String,
    val gender: String?,
    val birthDate: String,
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
    val createdAt: String,
    val updatedAt: String?
)

@Serializable
data class PersonsResponseDto(
    val persons: List<PersonResponseDto>,
    val pagination: PaginationResponseDto
)

@Serializable
data class CreatePersonRequestDto(
    val gender: String? = null,
    val birthDate: String,
    val citizenship: String? = null,
    val hasDualCitizenship: Boolean? = null,
    val nationality: String? = null,
    val nativeLanguage: String? = null,
    val speaksRussian: Boolean? = null,
    val otherLanguages: List<String> = emptyList(),
    val educationLevel: String? = null,
    val maritalStatus: String? = null,
    val childrenCount: Int? = null,
    val relationToHousehold: String? = null,
    val placeOfBirth: String? = null,
    val currentResidence: String? = null,
    val incomeSources: List<String> = emptyList(),
    val employmentStatus: String? = null
)

@Serializable
data class UpdatePersonRequestDto(
    val gender: String? = null,
    val birthDate: String? = null,
    val citizenship: String? = null,
    val hasDualCitizenship: Boolean? = null,
    val nationality: String? = null,
    val nativeLanguage: String? = null,
    val speaksRussian: Boolean? = null,
    val otherLanguages: List<String>? = null,
    val educationLevel: String? = null,
    val maritalStatus: String? = null,
    val childrenCount: Int? = null,
    val relationToHousehold: String? = null,
    val placeOfBirth: String? = null,
    val currentResidence: String? = null,
    val incomeSources: List<String>? = null,
    val employmentStatus: String? = null
)