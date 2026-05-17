package org.censusmate.domain.repository

import org.censusmate.data.dto.CreatePersonRequestDto
import org.censusmate.data.dto.UpdatePersonRequestDto
import org.censusmate.domain.model.Person
import java.util.UUID

interface PersonRepository {
    suspend fun findByHousehold(householdId: UUID, limit: Int = 10, offset: Int = 0): Pair<Int, List<Person>>
    suspend fun findById(id: UUID): Person?
    suspend fun create(householdId: UUID, dto: CreatePersonRequestDto): Person
    suspend fun update(id: UUID, dto: UpdatePersonRequestDto): Person?
    suspend fun delete(id: UUID): Boolean
}