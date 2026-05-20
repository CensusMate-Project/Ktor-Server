package org.censusmate.domain.usecase.stats

import org.censusmate.domain.model.EventStats
import org.censusmate.domain.repository.StatsRepository
import org.censusmate.utils.AppError
import java.util.UUID

class GetStatsUseCase(private val statsRepository: StatsRepository) {
    suspend operator fun invoke(eventId: UUID): EventStats =
        statsRepository.getEventStats(eventId)
            ?: throw AppError.NotFound("Event with id=$eventId not found")
}