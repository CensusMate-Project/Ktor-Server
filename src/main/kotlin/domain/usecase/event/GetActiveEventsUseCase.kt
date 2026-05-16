package org.censusmate.domain.usecase.event

import org.censusmate.domain.model.Event
import org.censusmate.domain.model.PaginationDto
import org.censusmate.domain.repository.EventRepository

class GetActiveEventsUseCase(private val repo: EventRepository) {
    suspend operator fun invoke(page: Int = 0, limit: Int = 10): Pair<List<Event>, PaginationDto> {
        val safePage = if (page < 1) 1 else page
        val safeLimit = if (limit < 1) 10 else limit
        val offset = (safePage - 1) * safeLimit

        val (total, events) = repo.findActive(safeLimit, offset)

        return Pair(events, PaginationDto(total, safeLimit, offset))
    }
}
