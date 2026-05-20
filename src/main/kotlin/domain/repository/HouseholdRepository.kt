package org.censusmate.domain.repository

import org.censusmate.domain.model.Household
import org.censusmate.domain.model.HouseholdData
import org.censusmate.domain.model.HouseholdUpdateData
import java.util.UUID

interface HouseholdRepository {
    suspend fun findAll(limit: Int = 10, offset: Int = 0): Pair<Int, List<Household>>
    suspend fun findByEnumerator(enumeratorId: UUID, limit: Int = 10, offset: Int = 0): Pair<Int, List<Household>>
    suspend fun findById(id: UUID): Household?
    suspend fun create(enumeratorId: UUID, eventId: UUID?, data: HouseholdData): Household
    suspend fun update(id: UUID, data: HouseholdUpdateData): Household?
    suspend fun delete(id: UUID): Boolean
}