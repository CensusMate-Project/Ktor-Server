package org.censusmate.domain.model

data class AddressSuggestion(
    val value: String,
    val unrestrictedValue: String,
    val isValid: Boolean
)