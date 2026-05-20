package org.censusmate.data.remote.dadata

import io.ktor.client.call.body
import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import io.ktor.http.appendPathSegments
import kotlinx.serialization.json.Json
import org.censusmate.domain.model.AddressSuggestion
import ru.marisov.dadata.client.extensions.auth.secret
import ru.marisov.dadata.client.extensions.auth.token
import ru.marisov.dadata.client.extensions.suggestions.*
import kotlin.collections.firstOrNull
import org.censusmate.data.remote.dadata.dto.DaDataSuggestResponse
import org.censusmate.data.remote.dadata.mapper.toDomain
import ru.marisov.dadata.client.SuggestionsDadataClient.Companion as DaDataEndpoints

class DaDataClient(private val apiKey: String, private val secretKey: String) {
    private val client = SuggestionsDadataClient {
        auth {
            token(apiKey)
            secret(secretKey)
        }
    }

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun validate(address: String): AddressSuggestion? =
        suggest(address, count = 1).firstOrNull()

    suspend fun suggest(query: String, count: Int = 5): List<AddressSuggestion> {
        val response = client {
            method = HttpMethod.Post
            url { appendPathSegments(DaDataEndpoints.END_POINT_SUGGEST_ADDRESS) }
            setBody("""{ "query": "$query", "count": $count }""")
        }

        val body = response.body<String>()
        val result = json.decodeFromString<DaDataSuggestResponse>(body)
        return result.suggestions.map { it.toDomain() }
    }
}