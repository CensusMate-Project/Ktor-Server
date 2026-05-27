package org.censusmate.domain.usecase.user

import org.censusmate.domain.model.User
import org.censusmate.domain.repository.UserRepository
import org.censusmate.utils.AppError
import java.util.UUID

class BlockUserUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(id: UUID, isBlocked: Boolean): User =
        userRepository.setBlocked(id, isBlocked)
            ?: throw AppError.NotFound("User with id=$id not found")
}