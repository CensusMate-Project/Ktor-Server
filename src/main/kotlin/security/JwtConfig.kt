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
import org.censusmate.Config.JwtConfig as Config

object JwtConfig {
    private lateinit var secret: String
    lateinit var issuer: String
    private lateinit var audience: String
    private var expirationHours: Long = 24

    lateinit var verifier: JWTVerifier
        private set

    fun init(config: Config) {
        this.secret = config.secret
        this.issuer = config.issuer
        this.audience = config.audience
        this.expirationHours = config.expirationHours
        this.verifier = JWT
            .require(Algorithm.HMAC256(secret))
            .withIssuer(issuer)
            .withAudience(audience)
            .build()
    }

    fun generateToken(user: User): String =
        JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withSubject(user.id.toString())
            .withClaim("email", user.email)
            .withClaim("role", user.role.toDbValue())
            .withExpiresAt(Date(System.currentTimeMillis() + expirationHours * 3_600_000L))
            .sign(Algorithm.HMAC256(secret))

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