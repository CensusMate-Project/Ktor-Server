package org.censusmate.data.mapper

import org.censusmate.data.database.tables.EventTable
import org.censusmate.data.dto.EventResponseDto
import org.censusmate.domain.model.Event
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toEvent() = Event(
    id = this[EventTable.id],
    name = this[EventTable.name],
    startDatetime = this[EventTable.startDatetime],
    endDatetime = this[EventTable.endDatetime],
    createdAt = this[EventTable.createdAt],
    updatedAt = this[EventTable.updatedAt]
)

fun Event.toResponseDto() = EventResponseDto(
    id = id.toString(),
    name = name,
    startDatetime = startDatetime.toString(),
    endDatetime = endDatetime.toString(),
    isActive = isActive,
    createdAt = createdAt.toString(),
    updatedAt = updatedAt?.toString()
)