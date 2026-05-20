package org.censusmate.domain.usecase.household

import org.censusmate.data.dto.CreateHouseholdRequestDto
import org.censusmate.domain.model.Household
import org.censusmate.domain.model.HouseholdData
import org.censusmate.domain.repository.EventRepository
import org.censusmate.domain.repository.HouseholdRepository
import org.censusmate.domain.usecase.suggest.SuggestAddressUseCase
import org.censusmate.security.principal.UserPrincipal
import org.censusmate.utils.AppError

class CreateHouseholdUseCase(
    private val householdRepo: HouseholdRepository,
    private val eventRepo: EventRepository,
    private val suggestAddressUseCase: SuggestAddressUseCase
) {
    suspend operator fun invoke(dto: CreateHouseholdRequestDto, principal: UserPrincipal): Household {
        if (dto.address.isBlank()) throw AppError.BadRequest("Address is required")
        if (dto.totalResidents < 1) throw AppError.BadRequest("total_residents must be at least 1")

        val suggestion = suggestAddressUseCase(dto.address, count = 1).firstOrNull()
            ?: throw AppError.BadRequest("Cannot find address: ${dto.address}")

        if (!suggestion.isValid)
            throw AppError.BadRequest("Address is not specific enough, please provide a house number")

        val activeEvent = eventRepo.findActive(limit = 1).second.firstOrNull()
            ?: throw AppError.BadRequest("No active census event")

        val data = HouseholdData(
            address = suggestion.unrestrictedValue,
            totalResidents = dto.totalResidents,
            dwellingType = dto.dwellingType,
            buildingYear = dto.buildingYear,
            totalArea = dto.totalArea,
            livingArea = dto.livingArea,
            roomsCount = dto.roomsCount,
            notes = dto.notes
        )

        return householdRepo.create(principal.userId, activeEvent.id, data)
    }
}