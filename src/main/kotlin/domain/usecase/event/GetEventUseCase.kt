package org.censusmate.domain.usecase.event

import org.censusmate.domain.model.Event
import org.censusmate.domain.repository.EventRepository
import org.censusmate.utils.AppError
import java.util.UUID

class GetEventUseCase(private val repo: EventRepository) {
    suspend operator fun invoke(id: UUID): Event =
        repo.findById(id) ?: throw AppError.NotFound("Event with id=$id not found")
}