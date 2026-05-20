package org.censusmate.domain.usecase.persons

import org.censusmate.domain.model.PaginationDto
import org.censusmate.domain.model.Person
import org.censusmate.domain.repository.PersonRepository
import java.util.UUID

class GetPersonsUseCase(private val personRepository: PersonRepository) {
    suspend operator fun invoke(householdId: UUID, page: Int = 1, limit: Int = 10): Pair<List<Person>, PaginationDto> {
        val safePage = if (page < 1) 1 else page
        val safeLimit = if (limit < 1) 10 else limit
        val offset = (safePage - 1) * safeLimit

        val (total, persons) = personRepository.findByHousehold(householdId, limit, offset)

        return Pair(persons, PaginationDto(total, safeLimit, offset))
    }
}