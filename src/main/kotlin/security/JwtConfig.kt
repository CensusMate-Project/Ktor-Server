package org.censusmate.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.JWTVerifier
import io.ktor.server.auth.jwt.JWTCredential
import org.censusmate.domain.model.RoleType
import org.censusmate.domain.model.User
import org.censusmate.security.principal.UserPrincipal
import java.util.Date
import java.util.UUID

object JwtConfig {
    private const val SECRET = "my-super-secret-key"
    const val ISSUER = "ktor-app"
    private const val AUDIENCE = "mobile-app"
    private const val VALIDITY = 7L * 24 * 60 * 60 * 1000 // 7 days

    val verifier: JWTVerifier = JWT
        .require(Algorithm.HMAC256(SECRET))
        .withAudience(AUDIENCE)
        .withIssuer(ISSUER)
        .build()

    fun generateToken(user: User): String =
        JWT.create()
            .withIssuer(ISSUER)
            .withAudience(AUDIENCE)
            .withSubject(user.id.toString())
            .withClaim("email", user.email)
            .withClaim("role", user.role.toDbValue())
            .withExpiresAt(Date(System.currentTimeMillis() + VALIDITY))
            .sign(Algorithm.HMAC256(SECRET))

    fun validateCredential(credential: JWTCredential): UserPrincipal? {
        val userId = credential.payload.subject
            ?.let { runCatching { UUID.fromString(it) }.getOrNull() }
            ?: return null

        val email = credential.payload.getClaim("email").asString()
            ?: return null

        val roleStr = credential.payload.getClaim("role").asString()
            ?: return null

        val role = runCatching { RoleType.fromDbValue(roleStr) }.getOrNull()
            ?: return null

        return UserPrincipal(userId, email, role)
    }
}