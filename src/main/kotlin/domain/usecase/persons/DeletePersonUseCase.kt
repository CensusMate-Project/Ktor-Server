package org.censusmate.domain.usecase.persons

import org.censusmate.domain.model.RoleType
import org.censusmate.domain.repository.HouseholdRepository
import org.censusmate.domain.repository.PersonRepository
import org.censusmate.security.principal.UserPrincipal
import org.censusmate.utils.AppError
import java.util.UUID

class DeletePersonUseCase(
    private val personRepository: PersonRepository,
    private val householdRepository: HouseholdRepository
) {
    suspend operator fun invoke(id: UUID, principal: UserPrincipal) {
        val person = personRepository.findById(id)
            ?: throw AppError.NotFound("Person with id=$id not found")

        val household = householdRepository.findById(person.householdId)
            ?: throw AppError.NotFound("Household for person with id=$id not found")

        if (principal.role != RoleType.ADMINISTRATOR && household.enumeratorId != principal.userId) {
            throw AppError.Forbidden("Access denied")
        }

        personRepository.delete(id)
    }
}