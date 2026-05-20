package org.censusmate.data.remote.dadata.mapper

import org.censusmate.data.remote.dadata.dto.DaDataSuggestion
import org.censusmate.domain.model.AddressSuggestion

fun DaDataSuggestion.toDomain() = AddressSuggestion(
    value = value,
    unrestrictedValue = unrestrictedValue,
    isValid = data.house != null
)