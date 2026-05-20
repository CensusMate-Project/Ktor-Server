package org.censusmate.domain.usecase.persons

import org.censusmate.domain.model.Person
import org.censusmate.domain.repository.PersonRepository
import org.censusmate.utils.AppError
import java.util.UUID

class GetPersonUseCase(private val personRepository: PersonRepository) {
    suspend operator fun invoke(id: UUID): Person =
        personRepository.findById(id) ?: throw AppError.NotFound("Person with id=$id not found")
}