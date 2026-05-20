package org.censusmate.domain.usecase.household

import org.censusmate.domain.model.Household
import org.censusmate.domain.repository.HouseholdRepository
import org.censusmate.utils.AppError
import java.util.UUID

class GetHouseholdUseCase(private val householdRepository: HouseholdRepository) {
    suspend operator fun invoke(id: UUID): Household {
        return householdRepository.findById(id) ?: throw AppError.NotFound("Household with id=$id not found")
    }
}