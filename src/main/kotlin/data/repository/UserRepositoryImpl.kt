package org.censusmate.data.repository

import org.censusmate.data.database.DatabaseFactory.dbTransactionQuery
import org.censusmate.data.database.tables.UserAuthTable
import org.censusmate.data.database.tables.UserTable
import org.censusmate.data.mapper.toUser
import org.censusmate.domain.model.User
import org.censusmate.domain.repository.UserRepository
import org.censusmate.security.PasswordHasher
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.util.UUID

class UserRepositoryImpl : UserRepository {
    override suspend fun findAll(
        limit: Int,
        offset: Int
    ): Pair<Int, List<User>> = dbTransactionQuery {
        val total = UserTable.selectAll().count().toInt()

        val users = UserTable
            .selectAll()
            .orderBy(UserTable.createdAt, SortOrder.DESC)
            .limit(limit).offset(offset.toLong())
            .map { it.toUser() }

        Pair(total, users)
    }

    override suspend fun findById(id: UUID): User? = dbTransactionQuery {
        UserTable
            .selectAll()
            .where { UserTable.id eq id }
            .singleOrNull()
            ?.toUser()
    }

    override suspend fun findByEmail(email: String): User? = dbTransactionQuery {
        UserTable
            .selectAll()
            .where { UserTable.email eq email }
            .singleOrNull()
            ?.toUser()
    }

    override suspend fun findPasswordHashByUserId(userId: UUID): String? = dbTransactionQuery {
        UserAuthTable
            .selectAll()
            .where { UserAuthTable.userId eq userId }
            .singleOrNull()
            ?.get(UserAuthTable.password_hash)
    }

    override suspend fun updateLastLogin(userId: UUID) = dbTransactionQuery {
        UserAuthTable.update({ UserAuthTable.userId eq userId }) {
            it[UserAuthTable.lastLogin] = LocalDateTime.now()
        }
        Unit
    }

    override suspend fun create(
        email: String,
        firstName: String,
        lastName: String,
        role: String,
        password: String
    ): User = dbTransactionQuery {
        val insertedId = UserTable.insert {
            it[UserTable.email] = email
            it[UserTable.firstName] = firstName
            it[UserTable.lastName] = lastName
            it[UserTable.role] = role
        }[UserTable.id]

        UserAuthTable.insert {
            it[UserAuthTable.userId] = insertedId
            it[UserAuthTable.password_hash] = PasswordHasher.hash(password)
        }

        UserTable
            .selectAll()
            .where { UserTable.id eq insertedId }
            .single()
            .toUser()
    }

    override suspend fun update(
        id: UUID,
        firstName: String?,
        lastName: String?,
        email: String?
    ): User? = dbTransactionQuery {
        val updatedCount = UserTable.update({ UserTable.id eq id }) { stmt ->
            firstName?.let { stmt[UserTable.firstName] = it }
            lastName?.let { stmt[UserTable.lastName] = it }
            email?.let { stmt[UserTable.email] = it }
        }

        if (updatedCount == 0) return@dbTransactionQuery null

        UserTable
            .selectAll()
            .where { UserTable.id eq id }
            .singleOrNull()
            ?.toUser()
    }

    override suspend fun setBlocked(id: UUID, isBlocked: Boolean): User? = dbTransactionQuery {
        val updatedCount = UserTable.update({ UserTable.id eq id }) {
            it[UserTable.isBlocked] = isBlocked
        }

        if (updatedCount == 0) return@dbTransactionQuery null

        UserTable
            .selectAll()
            .where { UserTable.id eq id }
            .singleOrNull()
            ?.toUser()
    }


}