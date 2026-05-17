package org.censusmate.domain.model

data class HouseholdData(
    val address: String,
    val totalResidents: Int,
    val dwellingType: String? = null,
    val buildingYear: String? = null,
    val totalArea: Int? = null,
    val livingArea: Int? = null,
    val roomsCount: Int? = null,
    val notes: String? = null
)

data class HouseholdUpdateData(
    val address: String? = null,
    val totalResidents: Int? = null,
    val dwellingType: String? = null,
    val buildingYear: String? = null,
    val totalArea: Int? = null,
    val livingArea: Int? = null,
    val roomsCount: Int? = null,
    val notes: String? = null
)