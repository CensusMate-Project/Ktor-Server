package org.censusmate.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PaginationResponseDto(
    val page: Int,
    val limit: Int,
    val total: Int,
    val pages: Int
)