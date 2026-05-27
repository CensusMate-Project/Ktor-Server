package org.censusmate.domain.usecase.user

import org.censusmate.data.dto.UpdateUserRequestDto
import org.censusmate.domain.model.User
import org.censusmate.domain.repository.UserRepository
import org.censusmate.utils.AppError
import java.util.UUID

class UpdateUserUseCase(
    private val userRepository: UserRepository,
    private val minPasswordLen: Int,
) {
    suspend operator fun invoke(id: UUID, dto: UpdateUserRequestDto): User {
        if (dto.email != null) {
            val existing = userRepository.findByEmail(dto.email)
            if (existing != null && existing.id != id)
                throw AppError.Conflict("Email '${dto.email}' is already in use")
        }

        if (dto.newPassword != null && dto.newPassword.length < minPasswordLen)
            throw AppError.BadRequest("Password must be at least 6 characters")

        return userRepository.update(
            id = id,
            firstName = dto.firstName?.trim(),
            lastName = dto.lastName?.trim(),
            email = dto.email?.trim(),
            newPassword = dto.newPassword
        ) ?: throw AppError.NotFound("User with id=$id not found")
    }
}
