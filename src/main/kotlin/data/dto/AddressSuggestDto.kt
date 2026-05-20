package org.censusmate.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class AddressSuggestResponseDto(
    val suggestions: List<AddressSuggestionDto>
)

@Serializable
data class AddressSuggestionDto(
    val value: String,
    val unrestrictedValue: String
)