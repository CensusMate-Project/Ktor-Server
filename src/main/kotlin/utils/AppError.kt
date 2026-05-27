package org.censusmate.utils

sealed class AppError(message: String) : Exception(message) {
    class NotFound(message: String = "Not found") : AppError(message)

    class Unauthorized(message: String = "Unauthorized") : AppError(message)

    class Forbidden(message: String = "Access forbidden") : AppError(message)

    class BadRequest(message: String = "Bad request") : AppError(message)

    class Conflict(message: String = "Conflict") : AppError(message)
}