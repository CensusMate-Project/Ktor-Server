package org.censusmate.controller

import io.github.smiley4.ktoropenapi.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.censusmate.data.dto.UserResponseDto
import org.censusmate.data.dto.UsersResponseDto
import org.censusmate.data.mapper.toResponseDto
import org.censusmate.domain.model.RoleType
import org.censusmate.domain.usecase.user.BlockUserUseCase
import org.censusmate.domain.usecase.user.CreateUserUseCase
import org.censusmate.domain.usecase.user.GetUserUseCase
import org.censusmate.domain.usecase.user.GetUsersUseCase
import org.censusmate.domain.usecase.user.UpdateUserUseCase
import org.censusmate.plugins.ErrorResponse
import org.censusmate.utils.requireRole
import org.censusmate.utils.uuidParam

class UserController(
    private val getUsersUseCase: GetUsersUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val blockUserUseCase: BlockUserUseCase
) {
    fun configure(route: Route) {
        route.apply {
            authenticate("auth-jwt") {
                get({
                    tags("Users")
                    summary = "All users list"
                    description = "Возвращает всех пользователей системы. **Только для администратора.**"
                    request {
                        queryParameter<Int>("page") {
                            description = "Номер страницы (начиная с 1)"
                            required = false
                        }
                        queryParameter<Int>("limit") {
                            description = "Количество записей на странице"
                            required = false
                        }
                    }
                    response {
                        HttpStatusCode.OK to {
                            description = "Список пользователей"
                            body<UsersResponseDto>()
                        }
                        HttpStatusCode.Forbidden to {
                            description = "Недостаточно прав"
                            body<ErrorResponse>()
                        }
                    }
                }) {
                    call.requireRole(RoleType.ADMINISTRATOR)
                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                    val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
                    val (users, pagination) = getUsersUseCase(page, limit)
                    call.respond(
                        HttpStatusCode.OK, UsersResponseDto(
                            users = users.map { it.toResponseDto() },
                            pagination = pagination.toResponseDto()
                        )
                    )
                }

                route("/{id}") {
                    get({
                        tags("Users")
                        summary = "Get user by ID"
                        description = "Возвращает пользователя по UUID. **Только для администратора.**"
                        request {
                            pathParameter<String>("id") {
                                description = "UUID пользователя"
                                example("Пример UUID") { value = "550e8400-e29b-41d4-a716-446655440000" }
                            }
                        }
                        response {
                            HttpStatusCode.OK to {
                                description = "Данные пользователя"
                                body<UserResponseDto>()
                            }
                            HttpStatusCode.NotFound to {
                                description = "Пользователь не найден"
                                body<ErrorResponse>()
                            }
                            HttpStatusCode.Forbidden to {
                                description = "Недостаточно прав"
                                body<ErrorResponse>()
                            }
                        }
                    }) {
                        call.requireRole(RoleType.ADMINISTRATOR)
                        val id = call.uuidParam("id")
                        val user = getUserUseCase(id)
                        call.respond(HttpStatusCode.OK, user.toResponseDto())
                    }
                }
            }
        }
    }
}