package org.censusmate.domain.usecase.event

import org.censusmate.data.dto.UpdateEventRequestDto
import org.censusmate.domain.model.Event
import org.censusmate.domain.repository.EventRepository
import org.censusmate.utils.AppError
import java.time.LocalDateTime
import java.util.UUID

class UpdateEventUseCase(private val repo: EventRepository) {
    suspend operator fun invoke(id: UUID, dto: UpdateEventRequestDto): Event {
        val existing = repo.findById(id)
            ?: throw AppError.NotFound("Event with id=$id not found")

        val start = dto.startDatetime?.let {
            runCatching { LocalDateTime.parse(it) }
                .getOrElse { throw AppError.BadRequest("Invalid start_datetime format") }
        } ?: existing.startDatetime

        val end = dto.endDatetime?.let {
            runCatching { LocalDateTime.parse(it) }
                .getOrElse { throw AppError.BadRequest("Invalid end_datetime format") }
        } ?: existing.endDatetime

        if (!end.isAfter(start))
            throw AppError.BadRequest("end_datetime must be after start_datetime")

        if (repo.hasOverlap(start, end, excludeId = id))
            throw AppError.Conflict("Event overlaps with an existing event")

        return repo.update(id, dto.name?.trim(), start, end)!!
    }
}