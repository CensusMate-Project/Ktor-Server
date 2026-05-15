package org.censusmate.data.dto

import kotlinx.serialization.SerialName
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
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    val role: String = "agent"
)

@Serializable
data class UpdateUserRequestDto(
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    val email: String? = null
)

@Serializable
data class BlockUserRequestDto(
    @SerialName("is_blocked")
    val isBlocked: Boolean
)