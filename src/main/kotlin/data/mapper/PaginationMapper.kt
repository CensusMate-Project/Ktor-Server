package org.censusmate.data.mapper

import org.censusmate.data.dto.PaginationResponseDto
import org.censusmate.domain.model.PaginationDto

fun PaginationDto.toResponseDto() = PaginationResponseDto(
    page = page,
    limit = limit,
    total = total,
    pages = pages
)