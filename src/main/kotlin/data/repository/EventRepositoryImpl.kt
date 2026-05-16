package org.censusmate.data.repository

import org.censusmate.data.database.DatabaseFactory.dbTransactionQuery
import org.censusmate.data.database.tables.EventTable
import org.censusmate.data.mapper.toEvent
import org.censusmate.domain.model.Event
import org.censusmate.domain.repository.EventRepository
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.util.UUID

class EventRepositoryImpl : EventRepository {
    override suspend fun findAll(limit: Int, offset: Int): Pair<Int, List<Event>> = dbTransactionQuery {
        val total = EventTable.selectAll().count().toInt()

        val events = EventTable.selectAll()
            .orderBy(EventTable.startDatetime, SortOrder.DESC)
            .map { it.toEvent() }

        Pair(total, events)
    }

    override suspend fun findActive(): List<Event> = dbTransactionQuery {
        val now = LocalDateTime.now()
        EventTable.selectAll()
            .where { (EventTable.startDatetime lessEq now) and (EventTable.endDatetime greaterEq now) }
            .map { it.toEvent() }
    }

    override suspend fun findById(id: UUID): Event? = dbTransactionQuery {
        EventTable.selectAll()
            .where { EventTable.id eq id }
            .singleOrNull()
            ?.toEvent()
    }

    override suspend fun create(
        name: String,
        startDatetime: LocalDateTime,
        endDatetime: LocalDateTime
    ): Event = dbTransactionQuery {
        val insertedId = EventTable.insert {
            it[EventTable.name] = name
            it[EventTable.startDatetime] = startDatetime
            it[EventTable.endDatetime] = endDatetime
        }[EventTable.id]

        EventTable.selectAll()
            .where { EventTable.id eq insertedId }
            .single()
            .toEvent()
    }

    override suspend fun update(
        id: UUID,
        name: String?,
        startDatetime: LocalDateTime?,
        endDatetime: LocalDateTime?
    ): Event? = dbTransactionQuery {
        val updatedCount = EventTable.update({ EventTable.id eq id }) { stmt ->
            name?.let { stmt[EventTable.name] = it }
            startDatetime?.let { stmt[EventTable.startDatetime] = it }
            endDatetime?.let { stmt[EventTable.endDatetime] = it }
        }
        if (updatedCount == 0) return@dbTransactionQuery null
        EventTable.selectAll().where { EventTable.id eq id }.singleOrNull()?.toEvent()
    }

    override suspend fun delete(id: UUID): Boolean = dbTransactionQuery {
        EventTable.deleteWhere { EventTable.id eq id } > 0
    }

    override suspend fun hasOverlap(
        start: LocalDateTime,
        end: LocalDateTime,
        excludeId: UUID?
    ): Boolean = dbTransactionQuery {
        EventTable.selectAll()
            .where {
                val overlap = (EventTable.startDatetime less end) and
                        (EventTable.endDatetime greater start)
                if (excludeId != null)
                    overlap and (EventTable.id neq excludeId)
                else
                    overlap
            }
            .any()
    }
}