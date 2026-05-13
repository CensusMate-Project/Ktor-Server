package org.censusmate.domain.usecase.auth

import org.censusmate.domain.model.User
import org.censusmate.domain.repository.UserRepository
import org.censusmate.utils.AppError
import java.util.UUID

class GetMeUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: UUID): User =
        userRepository.findById(userId)
            ?: throw AppError.NotFound("Authenticated user not found")
}
