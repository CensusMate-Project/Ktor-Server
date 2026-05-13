package org.censusmate.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserResponseDto(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: String,
    val isBlocked: Boolean,
    val createdAt: String,
    val updatedAt: String?
)

@Serializable
data class CreateUserRequestDto(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val role: String = "agent"
)

@Serializable
data class UpdateUserRequestDto(
    val firstName: String,
    val lastName: String,
    val email: String? = null
)

@Serializable
data class BlockUserRequestDto(
    val isBlocked: Boolean
)