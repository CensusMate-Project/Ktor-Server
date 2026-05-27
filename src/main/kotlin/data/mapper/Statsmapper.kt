package org.censusmate.data.mapper

import org.censusmate.data.dto.LanguageCountDto
import org.censusmate.data.dto.StatsResponseDto
import org.censusmate.domain.model.EventStats

fun EventStats.toResponseDto() = StatsResponseDto(
    eventId = eventId.toString(),
    eventName = eventName,
    totalPopulation = totalPopulation,
    totalHouseholds = totalHouseholds,
    avgPersonsPerHousehold = avgPersonsPerHousehold,
    genderDistribution = genderDistribution,
    averageAge = averageAge,
    childrenCount = childrenCount,
    elderlyCount = elderlyCount,
    maritalStatusDistribution = maritalStatusDistribution,
    avgChildrenCount = avgChildrenCount,
    educationDistribution = educationDistribution,
    employmentDistribution = employmentDistribution,
    incomeSourcesDistribution = incomeSourcesDistribution,
    percentSpeaksRussian = percentSpeaksRussian,
    dualCitizenshipCount = dualCitizenshipCount,
    topOtherLanguages = topOtherLanguages.map { LanguageCountDto(it.first, it.second) },
    dwellingTypeDistribution = dwellingTypeDistribution,
    avgTotalArea = avgTotalArea,
    avgLivingArea = avgLivingArea
)