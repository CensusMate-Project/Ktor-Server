package org.censusmate.domain.usecase.persons

import org.censusmate.data.dto.CreatePersonRequestDto
import org.censusmate.domain.model.Person
import org.censusmate.domain.model.PersonData
import org.censusmate.domain.repository.PersonRepository
import org.censusmate.utils.AppError
import java.time.LocalDate
import java.util.UUID

class CreatePersonUseCase(private val personRepository: PersonRepository) {
    suspend operator fun invoke(householdId: UUID, dto: CreatePersonRequestDto): Person {
        if (dto.birthDate.isBlank())
            throw AppError.BadRequest("birth_date is required")

        val birthDate = runCatching { LocalDate.parse(dto.birthDate) }
            .getOrElse { throw AppError.BadRequest("Invalid birth_date format. Expected: YYYY-MM-DD") }

        val data = PersonData(
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

        return personRepository.create(householdId, data)
    }
}