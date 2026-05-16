package org.censusmate.domain.repository

import org.censusmate.domain.model.User
import java.util.UUID

interface UserRepository {
    suspend fun findAll(limit: Int = 10, offset: Int = 0): Pair<Int, List<User>>
    suspend fun findById(id: UUID): User?
    suspend fun findByEmail(email: String): User?
    suspend fun findPasswordHashByUserId(userId: UUID): String?
    suspend fun updateLastLogin(userId: UUID)
    suspend fun create(email: String, firstName: String, lastName: String, role: String, password: String): User
    suspend fun update(
        id: UUID,
        firstName: String? = null,
        lastName: String? = null,
        email: String? = null,
        newPassword: String? = null
    ): User?

    suspend fun setBlocked(id: UUID, isBlocked: Boolean): User?
}
