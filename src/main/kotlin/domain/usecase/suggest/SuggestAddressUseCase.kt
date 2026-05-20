package org.censusmate.domain.usecase.suggest

import org.censusmate.data.remote.dadata.DaDataClient
import org.censusmate.domain.model.AddressSuggestion
import org.censusmate.utils.AppError

class SuggestAddressUseCase(private val daDataClient: DaDataClient) {
    suspend operator fun invoke(query: String, count: Int = 5): List<AddressSuggestion> {
        if (query.isBlank()) throw AppError.BadRequest("Query is required")

        return daDataClient.suggest(query, count)
    }
}