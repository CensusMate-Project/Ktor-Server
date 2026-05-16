package org.censusmate.domain.usecase.event

import org.censusmate.domain.repository.EventRepository
import org.censusmate.utils.AppError
import java.util.UUID

class DeleteEventUseCase(private val repo: EventRepository) {
    suspend operator fun invoke(id: UUID) {
        if (!repo.delete(id)) throw AppError.NotFound("Event with id=$id not found")
    }
}