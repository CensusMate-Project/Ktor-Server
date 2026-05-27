package org.censusmate.domain.repository

import org.censusmate.domain.model.Person
import org.censusmate.domain.model.PersonData
import org.censusmate.domain.model.PersonUpdateData
import java.util.UUID

interface PersonRepository {
    suspend fun findByHousehold(householdId: UUID, limit: Int = 10, offset: Int = 0): Pair<Int, List<Person>>
    suspend fun findById(id: UUID): Person?
    suspend fun create(householdId: UUID, data: PersonData): Person
    suspend fun update(id: UUID, data: PersonUpdateData): Person?
    suspend fun delete(id: UUID): Boolean
}