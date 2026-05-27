package org.censusmate.data.mapper

import org.censusmate.data.database.tables.UserTable
import org.censusmate.data.dto.MeResponseDto
import org.censusmate.data.dto.UserResponseDto
import org.censusmate.domain.model.RoleType
import org.censusmate.domain.model.User
import org.jetbrains.exposed.sql.ResultRow

fun User.toMeResponseDto() = MeResponseDto(
    id = id.toString(),
    email = email,
    firstName = firstName,
    lastName = lastName,
    role = role.toDbValue()
)

fun ResultRow.toUser() = User(
    id = this[UserTable.id],
    email = this[UserTable.email],
    firstName = this[UserTable.firstName],
    lastName = this[UserTable.lastName],
    role = RoleType.fromDbValue(this[UserTable.role]),
    isBlocked = this[UserTable.isBlocked],
    defaultUser = this[UserTable.defaultUser],
    createdAt = this[UserTable.createdAt],
    updatedAt = this[UserTable.updatedAt]
)

fun User.toResponseDto() = UserResponseDto(
    id = id.toString(),
    email = email,
    firstName = firstName,
    lastName = lastName,
    role = role.toDbValue(),
    isBlocked = isBlocked,
    createdAt = createdAt.toString(),
    updatedAt = updatedAt?.toString()
)
