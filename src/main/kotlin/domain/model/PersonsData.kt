package org.censusmate.domain.model

import java.time.LocalDate

data class PersonData(
    val gender: String? = null,
    val birthDate: LocalDate,
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

data class PersonUpdateData(
    val gender: String? = null,
    val birthDate: LocalDate? = null,
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