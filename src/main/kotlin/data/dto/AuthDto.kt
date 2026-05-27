package org.censusmate.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String
)

@Serializable
data class TokenResponseDto(
    val token: String,
    val tokenType: String = "bearer"
)

@Serializable
data class MeResponseDto(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: String
)