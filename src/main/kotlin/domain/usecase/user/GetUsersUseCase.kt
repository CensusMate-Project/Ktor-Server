package org.censusmate.domain.usecase.user

import org.censusmate.domain.model.PaginationDto
import org.censusmate.domain.model.User
import org.censusmate.domain.repository.UserRepository

class GetUsersUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(page: Int = 0, limit: Int = 10): Pair<List<User>, PaginationDto> {
        val safePage = if (page < 1) 1 else page
        val safeLimit = if (limit < 1) 10 else limit
        val offset = (safePage - 1) * safeLimit

        val (total, users) = userRepository.findAll(safeLimit, offset)

        return Pair(users, PaginationDto(total, safeLimit, offset))
    }
}