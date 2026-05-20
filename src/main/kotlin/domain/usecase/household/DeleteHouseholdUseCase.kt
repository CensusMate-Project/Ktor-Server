package org.censusmate.domain.usecase.household

import org.censusmate.domain.repository.HouseholdRepository
import java.util.UUID

class DeleteHouseholdUseCase(private val repo: HouseholdRepository) {
    suspend operator fun invoke(id: UUID) {
        if (!repo.delete(id)) throw IllegalArgumentException("Household with id=$id not found")
    }
}