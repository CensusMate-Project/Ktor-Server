package org.censusmate.security.principal

import org.censusmate.domain.model.RoleType
import java.security.Principal
import java.util.UUID

data class UserPrincipal(
    val userId: UUID,
    val email: String,
    val role: RoleType
) : Principal {
    override fun getName(): String {
        return email
    }
}