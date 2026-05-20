package org.censusmate.domain.usecase.persons

import org.censusmate.data.dto.UpdatePersonRequestDto
import org.censusmate.domain.model.Person
import org.censusmate.domain.model.PersonUpdateData
import org.censusmate.domain.model.RoleType
import org.censusmate.domain.repository.HouseholdRepository
import org.censusmate.domain.repository.PersonRepository
import org.censusmate.security.principal.UserPrincipal
import org.censusmate.utils.AppError
import java.time.LocalDate
import java.util.UUID

class UpdatePersonUseCase(
    private val personRepository: PersonRepository,
    private val householdRepository: HouseholdRepository
) {
    suspend operator fun invoke(id: UUID, dto: UpdatePersonRequestDto, principal: UserPrincipal): Person {
        val birthDate = dto.birthDate?.let {
            runCatching { LocalDate.parse(it) }
                .getOrElse { throw AppError.BadRequest("Invalid birth_date format. Expected: YYYY-MM-DD") }
        }

        val person = personRepository.findById(id)
            ?: throw AppError.NotFound("Person with id=$id not found")

        val household = householdRepository.findById(person.householdId)
            ?: throw AppError.NotFound("Household for person with id=$id not found")

        if (principal.role != RoleType.ADMINISTRATOR && household.enumeratorId != principal.userId) {
            throw AppError.Forbidden("Access denied")
        }

        val data = PersonUpdateData(
            gender = dto.gender,
            birthDate = birthDate,
            citizenship = dto.citizenship,
            hasDualCitizenship = dto.hasDualCitizenship,
            nationality = dto.nationality,
            nativeLanguage = dto.nativeLanguage,
            speaksRussian = dto.speaksRussian,
            otherLanguages = dto.otherLanguages,
            educationLevel = dto.educationLevel,
            maritalStatus = dto.maritalStatus,
            childrenCount = dto.childrenCount,
            relationToHousehold = dto.relationToHousehold,
            placeOfBirth = dto.placeOfBirth,
            currentResidence = dto.currentResidence,
            incomeSources = dto.incomeSources,
            employmentStatus = dto.employmentStatus
        )

        return personRepository.update(id, data) ?: throw AppError.NotFound("Person with id=$id not found")
    }
}