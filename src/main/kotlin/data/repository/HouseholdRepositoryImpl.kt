package org.censusmate.data.repository

import org.censusmate.data.database.DatabaseFactory.dbTransactionQuery
import org.censusmate.data.database.tables.HouseholdTable
import org.censusmate.data.mapper.toHousehold
import org.censusmate.domain.model.Household
import org.censusmate.domain.model.HouseholdData
import org.censusmate.domain.model.HouseholdUpdateData
import org.censusmate.domain.repository.HouseholdRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.util.UUID

class HouseholdRepositoryImpl : HouseholdRepository {
    override suspend fun findAll(limit: Int, offset: Int): Pair<Int, List<Household>> = dbTransactionQuery {
        val total = HouseholdTable.selectAll().count().toInt()

        val households = HouseholdTable.selectAll()
            .orderBy(HouseholdTable.createdAt, org.jetbrains.exposed.sql.SortOrder.DESC)
            .map { it.toHousehold() }

        Pair(total, households)
    }

    override suspend fun findByEnumerator(enumeratorId: UUID, limit: Int, offset: Int): Pair<Int, List<Household>> =
        dbTransactionQuery {
            val total = HouseholdTable.selectAll()
                .where { HouseholdTable.enumeratorId eq enumeratorId }
                .count().toInt()

            val households = HouseholdTable.selectAll()
                .where { HouseholdTable.enumeratorId eq enumeratorId }
                .orderBy(HouseholdTable.createdAt, org.jetbrains.exposed.sql.SortOrder.DESC)
                .map { it.toHousehold() }

            Pair(total, households)
        }

    override suspend fun findById(id: UUID): Household? = dbTransactionQuery {
        HouseholdTable.selectAll()
            .where { HouseholdTable.id eq id }
            .singleOrNull()
            ?.toHousehold()
    }

    override suspend fun create(enumeratorId: UUID, eventId: UUID?, data: HouseholdData): Household =
        dbTransactionQuery {
            val id = HouseholdTable.insert {
                it[HouseholdTable.enumeratorId] = enumeratorId
                it[HouseholdTable.eventId] = eventId
                it[HouseholdTable.address] = data.address
                it[HouseholdTable.totalResidents] = data.totalResidents
                it[HouseholdTable.dwellingType] = data.dwellingType
                it[HouseholdTable.buildingYear] = data.buildingYear
                it[HouseholdTable.totalArea] = data.totalArea
                it[HouseholdTable.livingArea] = data.livingArea
                it[HouseholdTable.roomsCount] = data.roomsCount
                it[HouseholdTable.notes] = data.notes
            }[HouseholdTable.id]

            HouseholdTable.selectAll().where { HouseholdTable.id eq id }.single().toHousehold()
        }

    override suspend fun update(id: UUID, data: HouseholdUpdateData): Household? = dbTransactionQuery {
        val count = HouseholdTable.update({ HouseholdTable.id eq id }) { stmt ->
            data.address?.let { stmt[HouseholdTable.address] = it }
            data.totalResidents?.let { stmt[HouseholdTable.totalResidents] = it }
            data.dwellingType?.let { stmt[HouseholdTable.dwellingType] = it }
            data.buildingYear?.let { stmt[HouseholdTable.buildingYear] = it }
            data.totalArea?.let { stmt[HouseholdTable.totalArea] = it }
            data.livingArea?.let { stmt[HouseholdTable.livingArea] = it }
            data.roomsCount?.let { stmt[HouseholdTable.roomsCount] = it }
            data.notes?.let { stmt[HouseholdTable.notes] = it }
        }
        if (count == 0) return@dbTransactionQuery null
        HouseholdTable.selectAll().where { HouseholdTable.id eq id }.singleOrNull()?.toHousehold()
    }

    override suspend fun delete(id: UUID): Boolean = dbTransactionQuery {
        HouseholdTable.deleteWhere { HouseholdTable.id eq id } > 0
    }
}