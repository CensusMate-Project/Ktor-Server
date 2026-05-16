package org.censusmate.domain.model

import org.censusmate.data.dto.PaginationResponseDto

data class PaginationDto(
    val page: Int,
    val limit: Int,
    val total: Int,
    val pages: Int
)

fun PaginationDto(
    total: Int,
    limit: Int,
    offset: Int
): PaginationDto {
    if (total == 0) {
        return PaginationDto(1, limit, 0, 0)
    }
    return PaginationDto(
        page = offset / limit + 1,
        limit = limit,
        total = total,
        pages = (total + limit - 1) / limit
    )
}