package org.censusmate.data.mapper

import org.censusmate.data.database.tables.PersonTable
import org.censusmate.data.dto.PersonResponseDto
import org.censusmate.domain.model.GenderType
import org.censusmate.domain.model.Person
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toPerson() = Person(
    id = this[PersonTable.id],
    householdId = this[PersonTable.householdId],
    gender = GenderType.fromDbValue(this[PersonTable.gender]),
    birthDate = this[PersonTable.birthDate],
    citizenship = this[PersonTable.citizenship],
    hasDualCitizenship = this[PersonTable.hasDualCitizenship],
    nationality = this[PersonTable.nationality],
    nativeLanguage = this[PersonTable.nativeLanguage],
    speaksRussian = this[PersonTable.speaksRussian],
    otherLanguages = this[PersonTable.otherLanguages] ?: emptyList(),
    educationLevel = this[PersonTable.educationLevel],
    maritalStatus = this[PersonTable.maritalStatus],
    childrenCount = this[PersonTable.childrenCount],
    relationToHousehold = this[PersonTable.relationToHousehold],
    placeOfBirth = this[PersonTable.placeOfBirth],
    currentResidence = this[PersonTable.currentResidence],
    incomeSources = this[PersonTable.incomeSources] ?: emptyList(),
    employmentStatus = this[PersonTable.employmentStatus],
    createdAt = this[PersonTable.createdAt],
    updatedAt = this[PersonTable.updatedAt]
)

fun Person.toResponseDto() = PersonResponseDto(
    id = id.toString(),
    householdId = householdId.toString(),
    gender = gender?.toDbValue(),
    birthDate = birthDate.toString(),
    citizenship = citizenship,
    hasDualCitizenship = hasDualCitizenship,
    nationality = nationality,
    nativeLanguage = nativeLanguage,
    speaksRussian = speaksRussian,
    otherLanguages = otherLanguages,
    educationLevel = educationLevel,
    maritalStatus = maritalStatus,
    childrenCount = childrenCount,
    relationToHousehold = relationToHousehold,
    placeOfBirth = placeOfBirth,
    currentResidence = currentResidence,
    incomeSources = incomeSources,
    employmentStatus = employmentStatus,
    createdAt = createdAt.toString(),
    updatedAt = updatedAt?.toString()
)