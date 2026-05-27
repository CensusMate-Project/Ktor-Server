package org.censusmate.data.repository

import org.censusmate.data.database.DatabaseFactory.dbTransactionQuery
import org.censusmate.data.database.tables.PersonTable
import org.censusmate.data.mapper.toPerson
import org.censusmate.domain.model.Person
import org.censusmate.domain.model.PersonData
import org.censusmate.domain.model.PersonUpdateData
import org.censusmate.domain.repository.PersonRepository
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.util.UUID

class PersonRepositoryImpl : PersonRepository {
    override suspend fun findByHousehold(householdId: UUID, limit: Int, offset: Int): Pair<Int, List<Person>> =
        dbTransactionQuery {
            val total = PersonTable.selectAll()
                .where { PersonTable.householdId eq householdId }
                .count().toInt()

            val persons = PersonTable.selectAll()
                .where { PersonTable.householdId eq householdId }
                .map { it.toPerson() }

            Pair(total, persons)
        }

    override suspend fun findById(id: UUID): Person? = dbTransactionQuery {
        PersonTable.selectAll()
            .where { PersonTable.id eq id }
            .singleOrNull()
            ?.toPerson()
    }

    override suspend fun create(householdId: UUID, data: PersonData): Person = dbTransactionQuery {
        val id = PersonTable.insert {
            it[PersonTable.householdId] = householdId
            it[PersonTable.gender] = data.gender
            it[PersonTable.birthDate] = data.birthDate
            it[PersonTable.citizenship] = data.citizenship
            it[PersonTable.hasDualCitizenship] = data.hasDualCitizenship
            it[PersonTable.nationality] = data.nationality
            it[PersonTable.nativeLanguage] = data.nativeLanguage
            it[PersonTable.speaksRussian] = data.speaksRussian
            it[PersonTable.otherLanguages] = data.otherLanguages.ifEmpty { null }
            it[PersonTable.educationLevel] = data.educationLevel
            it[PersonTable.maritalStatus] = data.maritalStatus
            it[PersonTable.childrenCount] = data.childrenCount
            it[PersonTable.relationToHousehold] = data.relationToHousehold
            it[PersonTable.placeOfBirth] = data.placeOfBirth
            it[PersonTable.currentResidence] = data.currentResidence
            it[PersonTable.incomeSources] = data.incomeSources.ifEmpty { null }
            it[PersonTable.employmentStatus] = data.employmentStatus
        }[PersonTable.id]

        PersonTable.selectAll().where { PersonTable.id eq id }.single().toPerson()
    }

    override suspend fun update(id: UUID, data: PersonUpdateData): Person? = dbTransactionQuery {
        val count = PersonTable.update({ PersonTable.id eq id }) { stmt ->
            data.gender?.let { stmt[PersonTable.gender] = it }
            data.birthDate?.let { stmt[PersonTable.birthDate] = it }
            data.citizenship?.let { stmt[PersonTable.citizenship] = it }
            data.hasDualCitizenship?.let { stmt[PersonTable.hasDualCitizenship] = it }
            data.nationality?.let { stmt[PersonTable.nationality] = it }
            data.nativeLanguage?.let { stmt[PersonTable.nativeLanguage] = it }
            data.speaksRussian?.let { stmt[PersonTable.speaksRussian] = it }
            data.otherLanguages?.let { stmt[PersonTable.otherLanguages] = it.ifEmpty { null } }
            data.educationLevel?.let { stmt[PersonTable.educationLevel] = it }
            data.maritalStatus?.let { stmt[PersonTable.maritalStatus] = it }
            data.childrenCount?.let { stmt[PersonTable.childrenCount] = it }
            data.relationToHousehold?.let { stmt[PersonTable.relationToHousehold] = it }
            data.placeOfBirth?.let { stmt[PersonTable.placeOfBirth] = it }
            data.currentResidence?.let { stmt[PersonTable.currentResidence] = it }
            data.incomeSources?.let { stmt[PersonTable.incomeSources] = it.ifEmpty { null } }
            data.employmentStatus?.let { stmt[PersonTable.employmentStatus] = it }
        }
        if (count == 0) return@dbTransactionQuery null
        PersonTable.selectAll().where { PersonTable.id eq id }.singleOrNull()?.toPerson()
    }

    override suspend fun delete(id: UUID): Boolean = dbTransactionQuery {
        TODO("Not yet implemented")
    }

}