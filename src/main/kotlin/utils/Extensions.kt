package org.censusmate.utils

import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.principal
import org.censusmate.domain.model.RoleType
import org.censusmate.security.principal.UserPrincipal
import java.util.UUID

fun ApplicationCall.uuidParam(name: String): UUID {
    val raw = parameters[name] ?: throw AppError.BadRequest("Missing path parameter: $name")
    return try {
        UUID.fromString(raw)
    } catch (e: IllegalArgumentException) {
        throw AppError.BadRequest("Invalid UUID format for parameter: $name")
    }
}

fun ApplicationCall.requirePrincipal(): UserPrincipal =
    principal<UserPrincipal>() ?: throw AppError.Unauthorized()

fun ApplicationCall.requireRole(vararg roles: RoleType) {
    val principal = requirePrincipal()
    if (principal.role !in roles) {
        throw AppError.Forbidden("This action requires one of the following roles: ${roles.joinToString()}")
    }
}