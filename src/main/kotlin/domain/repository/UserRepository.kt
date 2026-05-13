package org.censusmate.domain.repository

import org.censusmate.domain.model.User
import java.util.UUID

interface UserRepository {
    suspend fun findById(id: UUID): User?
    suspend fun findByEmail(email: String): User?
    suspend fun findPasswordHashByUserId(userId: UUID): String?
    suspend fun updateLastLogin(userId: UUID)
    suspend fun create(email: String, firstName: String, lastName: String, role: String, password: String): User
}
