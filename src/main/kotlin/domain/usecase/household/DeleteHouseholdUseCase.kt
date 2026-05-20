package org.censusmate.domain.usecase.household

import org.censusmate.domain.model.RoleType
import org.censusmate.domain.repository.HouseholdRepository
import org.censusmate.security.principal.UserPrincipal
import org.censusmate.utils.AppError
import java.util.UUID

class DeleteHouseholdUseCase(private val repo: HouseholdRepository) {
    suspend operator fun invoke(id: UUID, principal: UserPrincipal) {
        val existing = repo.findById(id)
            ?: throw AppError.NotFound("Household with id=$id not found")

        if (principal.role != RoleType.ADMINISTRATOR && existing.enumeratorId != principal.userId)
            throw AppError.Forbidden("Access denied")

        repo.delete(id)
    }
}