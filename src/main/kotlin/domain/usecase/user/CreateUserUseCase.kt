package org.censusmate.domain.usecase.user

import org.censusmate.data.dto.CreateUserRequestDto
import org.censusmate.domain.model.RoleType
import org.censusmate.domain.model.User
import org.censusmate.domain.repository.UserRepository
import org.censusmate.utils.AppError

class CreateUserUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(dto: CreateUserRequestDto): User {
        val roles = RoleType.getAllRoles()

        if (dto.email.isBlank())
            throw AppError.BadRequest("Email is required")

        if (dto.password.length < 6)
            throw AppError.BadRequest("Password must be at least 6 characters")

        if (dto.role !in roles)
            throw AppError.BadRequest("Role must be one of: ${roles.joinToString()}")

        if (userRepository.findByEmail(dto.email) != null)
            throw AppError.Conflict("A user with email '${dto.email}' already exists")

        return userRepository.create(
            email = dto.email.trim(),
            firstName = dto.firstName.trim(),
            lastName = dto.lastName.trim(),
            role = dto.role,
            password = dto.password
        )
    }
}