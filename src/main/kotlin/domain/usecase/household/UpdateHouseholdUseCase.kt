package org.censusmate.domain.usecase.household

import org.censusmate.data.dto.UpdateHouseholdRequestDto
import org.censusmate.domain.model.Household
import org.censusmate.domain.model.HouseholdUpdateData
import org.censusmate.domain.model.RoleType
import org.censusmate.domain.repository.HouseholdRepository
import org.censusmate.domain.usecase.suggest.SuggestAddressUseCase
import org.censusmate.security.principal.UserPrincipal
import org.censusmate.utils.AppError
import java.util.UUID

class UpdateHouseholdUseCase(
    private val householdRepository: HouseholdRepository,
    private val suggestAddressUseCase: SuggestAddressUseCase
) {
    suspend operator fun invoke(
        id: UUID,
        dto: UpdateHouseholdRequestDto,
        principal: UserPrincipal
    ): Household {
        val existing = householdRepository.findById(id) ?: throw AppError.NotFound("Household with id=${id} not found")

        if (principal.role != RoleType.ADMINISTRATOR && existing.enumeratorId != principal.userId)
            throw AppError.Forbidden("Access denied")

        val normalizedAddress = dto.address?.let { address ->
            if (address.isBlank()) throw AppError.BadRequest("Address cannot be empty")

            val suggestion = suggestAddressUseCase(address, count = 1).firstOrNull()
                ?: throw AppError.BadRequest("Cannot find address: ${dto.address}")

            if (!suggestion.isValid)
                throw AppError.BadRequest("Address is not specific enough, please provide a house number")

            suggestion.unrestrictedValue
        }

        val data = HouseholdUpdateData(
            address = normalizedAddress,
            totalResidents = dto.totalResidents,
            dwellingType = dto.dwellingType,
            buildingYear = dto.buildingYear,
            totalArea = dto.totalArea,
            livingArea = dto.livingArea,
            roomsCount = dto.roomsCount,
            notes = dto.notes
        )

        return householdRepository.update(id, data) ?: throw AppError.NotFound("Household with id=${id} not found")
    }
}