package org.censusmate.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import org.censusmate.utils.AppError

@Serializable
data class ErrorResponse(
    val error: String,
    val message: String
)

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<AppError.NotFound> { call, cause ->
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse("NOT_FOUND", cause.message ?: "Not found")
            )
        }
        exception<AppError.Unauthorized> { call, cause ->
            call.respond(
                HttpStatusCode.Unauthorized,
                ErrorResponse("UNAUTHORIZED", cause.message ?: "Unauthorized")
            )
        }
        exception<AppError.Forbidden> { call, cause ->
            call.respond(
                HttpStatusCode.Forbidden,
                ErrorResponse("FORBIDDEN", cause.message ?: "Forbidden")
            )
        }
        exception<AppError.BadRequest> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("BAD_REQUEST", cause.message ?: "Bad request")
            )
        }
        exception<AppError.Conflict> { call, cause ->
            call.respond(
                HttpStatusCode.Conflict,
                ErrorResponse("CONFLICT", cause.message ?: "Conflict")
            )
        }
        exception<Throwable> { call, cause ->
            call.application.environment.log.error("Unhandled exception", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred")
            )
        }
        status(HttpStatusCode.Unauthorized) { call, _ ->
            call.respond(
                HttpStatusCode.Unauthorized,
                ErrorResponse("UNAUTHORIZED", "Authentication required")
            )
        }
    }
}