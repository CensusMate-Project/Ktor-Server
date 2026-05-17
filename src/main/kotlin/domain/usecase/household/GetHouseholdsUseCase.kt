package org.censusmate.domain.usecase.household

import org.censusmate.domain.model.Household
import org.censusmate.domain.model.PaginationDto
import org.censusmate.domain.repository.HouseholdRepository

class GetHouseholdsUseCase(private val repo: HouseholdRepository) {
    suspend operator fun invoke(page: Int = 0, limit: Int = 10): Pair<List<Household>, PaginationDto> {
        val safePage = if (page < 1) 1 else page
        val safeLimit = if (limit < 1) 10 else limit
        val offset = (safePage - 1) * safeLimit

        val (total, households) = repo.findAll(safeLimit, offset)

        return Pair(households, PaginationDto(total, safeLimit, offset))
    }
}