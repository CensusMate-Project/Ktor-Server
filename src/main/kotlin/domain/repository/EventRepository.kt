package org.censusmate.domain.repository

import org.censusmate.domain.model.Event
import java.time.LocalDateTime
import java.util.UUID

interface EventRepository {
    suspend fun findAll(limit: Int = 10, offset: Int = 0): Pair<Int, List<Event>>
    suspend fun findActive(): List<Event>
    suspend fun findById(id: UUID): Event?
    suspend fun create(name: String, startDatetime: LocalDateTime, endDatetime: LocalDateTime): Event
    suspend fun update(id: UUID, name: String?, startDatetime: LocalDateTime?, endDatetime: LocalDateTime?): Event?
    suspend fun delete(id: UUID): Boolean
}
