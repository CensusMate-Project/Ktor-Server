package org.censusmate.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import org.censusmate.security.JwtConfig

fun Application.configureAuthentication() {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = JwtConfig.ISSUER
            verifier { JwtConfig.verifier }
            validate { credential ->
                JwtConfig.validateCredential(credential)
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid or expired token"))
            }
        }
    }
}