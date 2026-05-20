package org.censusmate.data.mapper

import org.censusmate.data.database.tables.HouseholdTable
import org.censusmate.data.dto.HouseholdResponseDto
import org.censusmate.domain.model.Household
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toHousehold() = Household(
    id = this[HouseholdTable.id],
    enumeratorId = this[HouseholdTable.enumeratorId],
    eventId = this[HouseholdTable.eventId],
    address = this[HouseholdTable.address],
    totalResidents = this[HouseholdTable.totalResidents],
    dwellingType = this[HouseholdTable.dwellingType],
    buildingYear = this[HouseholdTable.buildingYear],
    totalArea = this[HouseholdTable.totalArea],
    livingArea = this[HouseholdTable.livingArea],
    roomsCount = this[HouseholdTable.roomsCount],
    notes = this[HouseholdTable.notes],
    createdAt = this[HouseholdTable.createdAt],
    updatedAt = this[HouseholdTable.updatedAt]
)

fun Household.toResponseDto() = HouseholdResponseDto(
    id = id.toString(),
    enumeratorId = enumeratorId?.toString(),
    eventId = eventId?.toString(),
    address = address,
    totalResidents = totalResidents,
    dwellingType = dwellingType,
    buildingYear = buildingYear,
    totalArea = totalArea,
    livingArea = livingArea,
    roomsCount = roomsCount,
    notes = notes,
    createdAt = createdAt.toString(),
    updatedAt = updatedAt?.toString()
)