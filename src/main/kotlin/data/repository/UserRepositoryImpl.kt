package org.censusmate.data.repository

import org.censusmate.data.database.DatabaseFactory.dbTransactionQuery
import org.censusmate.data.database.tables.UserAuthTable
import org.censusmate.data.database.tables.UserTable
import org.censusmate.data.mapper.toUser
import org.censusmate.domain.model.User
import org.censusmate.domain.repository.UserRepository
import org.censusmate.security.PasswordHasher
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.util.UUID

class UserRepositoryImpl : UserRepository {
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
}