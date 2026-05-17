package org.censusmate.domain.repository

import org.censusmate.domain.model.Household
import java.util.UUID

interface HouseholdRepository {
    suspend fun findAll(limit: Int = 10, offset: Int = 0): Pair<Int, List<Household>>
    suspend fun findByEnumerator(enumeratorId: UUID, limit: Int = 10, offset: Int = 0): Pair<Int, List<Household>>
    suspend fun findById(id: UUID): Household?
    suspend fun create(
        enumeratorId: UUID,
        eventId: UUID?,
        address: String,
        totalResidents: Int,
        dwellingType: String?,
        buildingYear: String?,
        totalArea: Int?,
        livingArea: Int?,
        roomsCount: Int?,
        notes: String?
    ): Household

    suspend fun update(
        id: UUID,
        address: String?,
        totalResidents: Int?,
        dwellingType: String?,
        buildingYear: String?,
        totalArea: Int?,
        livingArea: Int?,
        roomsCount: Int?,
        notes: String?
    ): Household?

    suspend fun delete(id: UUID): Boolean
}