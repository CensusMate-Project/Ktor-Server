package org.censusmate.data.repository

import org.censusmate.data.database.DatabaseFactory.dbTransactionQuery
import org.censusmate.data.database.tables.EventTable
import org.censusmate.data.database.tables.HouseholdTable
import org.censusmate.data.database.tables.PersonTable
import org.censusmate.domain.model.EventStats
import org.censusmate.domain.repository.StatsRepository
import org.jetbrains.exposed.sql.selectAll
import java.time.LocalDate
import java.time.Period
import java.util.UUID

class StatsRepositoryImpl : StatsRepository {
    override suspend fun getEventStats(eventId: UUID): EventStats? = dbTransactionQuery {
        val event =
            EventTable.selectAll().where { EventTable.id eq eventId }.singleOrNull() ?: return@dbTransactionQuery null

        val households = HouseholdTable.selectAll().where { HouseholdTable.eventId eq eventId }.toList()

        val householdIds = households.map { it[HouseholdTable.id] }

        if (householdIds.isEmpty()) {
            return@dbTransactionQuery EventStats(
                eventId = eventId,
                eventName = event[EventTable.name],
                totalPopulation = 0,
                totalHouseholds = 0,
                avgPersonsPerHousehold = 0.0,
                genderDistribution = emptyMap(),
                averageAge = 0.0,
                childrenCount = 0,
                elderlyCount = 0,
                maritalStatusDistribution = emptyMap(),
                avgChildrenCount = 0.0,
                educationDistribution = emptyMap(),
                employmentDistribution = emptyMap(),
                incomeSourcesDistribution = emptyMap(),
                percentSpeaksRussian = 0.0,
                dualCitizenshipCount = 0,
                topOtherLanguages = emptyList(),
                dwellingTypeDistribution = emptyMap(),
                avgTotalArea = 0.0,
                avgLivingArea = 0.0
            )
        }

        val persons = PersonTable.selectAll().where { PersonTable.householdId inList householdIds }.toList()

        val totalHouseholds = households.size.toLong()
        val totalPopulation = persons.size.toLong()

        val avgPersonsPerHousehold = if (totalHouseholds > 0) totalPopulation.toDouble() / totalHouseholds else 0.0

        val today = LocalDate.now()
        val ages = persons.map {
            val birth = it[PersonTable.birthDate]
            Period.between(birth, today).years
        }
        val averageAge = if (ages.isNotEmpty()) ages.average() else 0.0
        val childrenCount = ages.count { it < 18 }.toLong()
        val elderlyCount = ages.count { it >= 65 }.toLong()

        val genderDistribution =
            persons.groupBy { it[PersonTable.gender] ?: "unknown" }.mapValues { it.value.size.toLong() }

        val maritalStatusDistribution =
            persons.mapNotNull { it[PersonTable.maritalStatus] }.groupBy { it }.mapValues { it.value.size.toLong() }

        val avgChildrenCount =
            persons.mapNotNull { it[PersonTable.childrenCount] }.let { if (it.isNotEmpty()) it.average() else 0.0 }

        val educationDistribution =
            persons.mapNotNull { it[PersonTable.educationLevel] }.groupBy { it }.mapValues { it.value.size.toLong() }

        val employmentDistribution =
            persons.mapNotNull { it[PersonTable.employmentStatus] }.groupBy { it }.mapValues { it.value.size.toLong() }

        val incomeSourcesDistribution = persons.flatMap { it[PersonTable.incomeSources] ?: emptyList() }.groupBy { it }
            .mapValues { it.value.size.toLong() }

        val speaksRussianCount = persons.count { it[PersonTable.speaksRussian] == true }
        val percentSpeaksRussian =
            if (totalPopulation > 0) speaksRussianCount.toDouble() / totalPopulation * 100.0 else 0.0

        val dualCitizenshipCount = persons.count { it[PersonTable.hasDualCitizenship] == true }.toLong()

        val topOtherLanguages = persons.flatMap { it[PersonTable.otherLanguages] ?: emptyList() }.groupBy { it }
            .mapValues { it.value.size.toLong() }.entries.sortedByDescending { it.value }.take(5)
            .map { it.key to it.value }

        val dwellingTypeDistribution = households.mapNotNull { it[HouseholdTable.dwellingType] }.groupBy { it }
            .mapValues { it.value.size.toLong() }

        val avgTotalArea =
            households.mapNotNull { it[HouseholdTable.totalArea] }.let { if (it.isNotEmpty()) it.average() else 0.0 }

        val avgLivingArea =
            households.mapNotNull { it[HouseholdTable.livingArea] }.let { if (it.isNotEmpty()) it.average() else 0.0 }

        EventStats(
            eventId = eventId,
            eventName = event[EventTable.name],
            totalPopulation = totalPopulation,
            totalHouseholds = totalHouseholds,
            avgPersonsPerHousehold = avgPersonsPerHousehold,
            genderDistribution = genderDistribution,
            averageAge = averageAge.round(1),
            childrenCount = childrenCount,
            elderlyCount = elderlyCount,
            maritalStatusDistribution = maritalStatusDistribution,
            avgChildrenCount = avgChildrenCount.round(2),
            educationDistribution = educationDistribution,
            employmentDistribution = employmentDistribution,
            incomeSourcesDistribution = incomeSourcesDistribution,
            percentSpeaksRussian = percentSpeaksRussian.round(2),
            dualCitizenshipCount = dualCitizenshipCount,
            topOtherLanguages = topOtherLanguages,
            dwellingTypeDistribution = dwellingTypeDistribution,
            avgTotalArea = avgTotalArea.round(1),
            avgLivingArea = avgLivingArea.round(1)
        )
    }
}

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return kotlin.math.round(this * multiplier) / multiplier
}