package org.censusmate.domain.usecase.event

import org.censusmate.data.dto.CreateEventRequestDto
import org.censusmate.domain.model.Event
import org.censusmate.domain.repository.EventRepository
import org.censusmate.utils.AppError
import java.time.LocalDateTime

class CreateEventUseCase(private val repo: EventRepository) {
    suspend operator fun invoke(dto: CreateEventRequestDto): Event {
        if (dto.name.isBlank()) throw AppError.BadRequest("Name is required")

        val start = runCatching { LocalDateTime.parse(dto.startDatetime) }
            .getOrElse { throw AppError.BadRequest("Invalid start_datetime format") }

        val end = runCatching { LocalDateTime.parse(dto.endDatetime) }
            .getOrElse { throw AppError.BadRequest("Invalid end_datetime format") }

        if (!end.isAfter(start))
            throw AppError.BadRequest("end_datetime must be after start_datetime")

        if (repo.hasOverlap(start, end))
            throw AppError.Conflict("Event overlaps with an existing event")

        return repo.create(dto.name.trim(), start, end)
    }
}