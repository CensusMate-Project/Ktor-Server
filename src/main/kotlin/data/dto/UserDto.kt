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
data class UsersResponseDto(
    val users: List<UserResponseDto>,
    val pagination: PaginationResponseDto
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
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val newPassword: String? = null
)

@Serializable
data class BlockUserRequestDto(
    val isBlocked: Boolean
)