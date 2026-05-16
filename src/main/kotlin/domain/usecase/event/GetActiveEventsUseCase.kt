package org.censusmate.domain.usecase.event

import org.censusmate.domain.model.Event
import org.censusmate.domain.repository.EventRepository

class GetActiveEventsUseCase(private val repo: EventRepository) {
    suspend operator fun invoke(): List<Event> = repo.findActive()
}
