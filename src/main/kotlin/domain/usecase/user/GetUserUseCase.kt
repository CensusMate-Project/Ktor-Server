package org.censusmate.domain.usecase.user

import org.censusmate.domain.model.User
import org.censusmate.domain.repository.UserRepository
import org.censusmate.utils.AppError
import java.util.UUID

class GetUserUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(id: UUID): User =
        userRepository.findById(id)
            ?: throw AppError.NotFound("User with id=$id not found")
}
