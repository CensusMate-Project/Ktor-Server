package org.censusmate.domain.usecase.auth

import org.censusmate.domain.model.User
import org.censusmate.domain.repository.UserRepository
import org.censusmate.security.JwtConfig
import org.censusmate.security.PasswordHasher
import org.censusmate.utils.AppError

class LoginUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(email: String, password: String): Pair<User, String> {
        val user = userRepository.findByEmail(email)
            ?: throw AppError.Unauthorized("Invalid email or password")

        if (user.isBlocked)
            throw AppError.Forbidden("Your account has been blocked. Contact an administrator")

        val storedHash = userRepository.findPasswordHashByUserId(user.id)
            ?: throw AppError.Unauthorized("Invalid email or password")

        if (!PasswordHasher.verify(password, storedHash))
            throw AppError.Unauthorized("Invalid email or password")

        userRepository.updateLastLogin(user.id)

        val token = JwtConfig.generateToken(user)
        return Pair(user, token)
    }
}