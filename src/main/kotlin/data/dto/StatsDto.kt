package org.censusmate.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class StatsResponseDto(
    val eventId: String,
    val eventName: String,

    // All info
    val totalPopulation: Long,
    val totalHouseholds: Long,
    val avgPersonsPerHousehold: Double,

    // Structure of the population
    val genderDistribution: Map<String, Long>,
    val averageAge: Double,
    val childrenCount: Long,
    val elderlyCount: Long,

    // Families and marital status
    val maritalStatusDistribution: Map<String, Long>,
    val avgChildrenCount: Double,

    // Education and employment
    val educationDistribution: Map<String, Long>,
    val employmentDistribution: Map<String, Long>,
    val incomeSourcesDistribution: Map<String, Long>,

    // Language and nationality
    val percentSpeaksRussian: Double,
    val dualCitizenshipCount: Long,
    val topOtherLanguages: List<LanguageCountDto>,

    // Housing
    val dwellingTypeDistribution: Map<String, Long>,
    val avgTotalArea: Double,
    val avgLivingArea: Double
)

@Serializable
data class LanguageCountDto(
    val language: String, val count: Long
)