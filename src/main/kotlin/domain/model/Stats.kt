package org.censusmate.domain.model

import java.util.UUID

data class EventStats(
    val eventId: UUID,
    val eventName: String,

    val totalPopulation: Long,
    val totalHouseholds: Long,
    val avgPersonsPerHousehold: Double,

    val genderDistribution: Map<String, Long>,
    val averageAge: Double,
    val childrenCount: Long,
    val elderlyCount: Long,

    val maritalStatusDistribution: Map<String, Long>,
    val avgChildrenCount: Double,

    val educationDistribution: Map<String, Long>,
    val employmentDistribution: Map<String, Long>,
    val incomeSourcesDistribution: Map<String, Long>,

    val percentSpeaksRussian: Double,
    val dualCitizenshipCount: Long,
    val topOtherLanguages: List<Pair<String, Long>>,

    val dwellingTypeDistribution: Map<String, Long>,
    val avgTotalArea: Double,
    val avgLivingArea: Double
)