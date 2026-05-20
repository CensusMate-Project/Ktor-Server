package org.censusmate.domain.repository

import org.censusmate.domain.model.EventStats
import java.util.UUID

interface StatsRepository {
    suspend fun getEventStats(eventId: UUID): EventStats?
}