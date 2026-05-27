package org.censusmate.data.remote.dadata.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DaDataSuggestResponse(
    val suggestions: List<DaDataSuggestion>
)

@Serializable
data class DaDataSuggestion(
    val value: String,
    @SerialName("unrestricted_value")
    val unrestrictedValue: String,
    val data: DaDataAddressData
)

@Serializable
data class DaDataAddressData(
    @SerialName("postal_code") val postalCode: String? = null,
    val country: String? = null,
    val region: String? = null,
    @SerialName("region_with_type") val regionWithType: String? = null,
    val city: String? = null,
    val street: String? = null,
    @SerialName("street_with_type") val streetWithType: String? = null,
    val house: String? = null,
    val flat: String? = null,
    @SerialName("fias_level") val fiasLevel: String? = null
)