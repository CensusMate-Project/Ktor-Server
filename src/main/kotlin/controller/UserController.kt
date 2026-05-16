package org.censusmate.controller

import io.github.smiley4.ktoropenapi.*
import io.ktor.http.*
import io.ktor.server.auth.authenticate
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.censusmate.data.dto.BlockUserRequestDto
import org.censusmate.data.dto.CreateUserRequestDto
import org.censusmate.data.dto.UpdateUserRequestDto
import org.censusmate.data.dto.UserResponseDto
import org.censusmate.data.dto.UsersResponseDto
import org.censusmate.data.mapper.toResponseDto
import org.censusmate.domain.model.RoleType
import org.censusmate.domain.usecase.user.BlockUserUseCase
import org.censusmate.domain.usecase.user.CreateUserUseCase
import org.censusmate.domain.usecase.user.GetUserUseCase
import org.censusmate.domain.usecase.user.GetUsersUseCase
import org.censusmate.domain.usecase.user.UpdateUserUseCase
import org.censusmate.plugins.ErrorResponseDto
import org.censusmate.utils.AppError
import org.censusmate.utils.callerId
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
                route("/users") {
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
                                body<ErrorResponseDto>()
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

                    post({
                        tags("Users")
                        summary = "Create new user"
                        description = "Создает нового пользователя. **Только для администратора.**"
                        request {
                            body<CreateUserRequestDto> {
                                description = "Данные нового пользователя"
                                example("Создать переписчика") {
                                    value = CreateUserRequestDto(
                                        email = "agent@census.ru",
                                        password = "secret123",
                                        firstName = "Иван",
                                        lastName = "Иванов",
                                        role = "agent"
                                    )
                                }
                            }
                        }
                        response {
                            HttpStatusCode.Created to {
                                description = "Пользователь успешно создан"
                                body<UserResponseDto>()
                            }
                            HttpStatusCode.Conflict to {
                                description = "Пользователь с таким email уже существует"
                                body<ErrorResponseDto>()
                            }
                            HttpStatusCode.BadRequest to {
                                description = "Некорректные данные (пустой email, короткий пароль, неверная роль)"
                                body<ErrorResponseDto>()
                            }
                            HttpStatusCode.Forbidden to {
                                description = "Недостаточно прав"
                                body<ErrorResponseDto>()
                            }
                        }
                    }) {
                        call.requireRole(RoleType.ADMINISTRATOR)
                        val dto = call.receive<CreateUserRequestDto>()
                        val user = createUserUseCase(dto)
                        call.respond(HttpStatusCode.Created, user.toResponseDto())
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
                                    body<ErrorResponseDto>()
                                }
                                HttpStatusCode.Forbidden to {
                                    description = "Недостаточно прав"
                                    body<ErrorResponseDto>()
                                }
                            }
                        }) {
                            call.requireRole(RoleType.ADMINISTRATOR)
                            val id = call.uuidParam("id")
                            val user = getUserUseCase(id)
                            call.respond(HttpStatusCode.OK, user.toResponseDto())
                        }

                        put({
                            tags("Users")
                            summary = "Update user"
                            description = "Обновляет поля профиля пользователя. " +
                                    "Передавать только те поля, которые нужно изменить. " +
                                    "**Только для администратора.**"
                            request {
                                pathParameter<String>("id") {
                                    description = "UUID пользователя"
                                }
                                body<UpdateUserRequestDto> {
                                    description = "Обновляемые поля (все опциональны)"
                                    example("Изменить имя") {
                                        value = UpdateUserRequestDto(
                                            firstName = "Иван",
                                            lastName = "Иванов",
                                            email = "agent@census.ru"
                                        )
                                    }
                                }
                            }
                            response {
                                HttpStatusCode.OK to {
                                    description = "Пользователь обновлён"
                                    body<UserResponseDto>()
                                }
                                HttpStatusCode.NotFound to {
                                    description = "Пользователь не найден"
                                    body<ErrorResponseDto>()
                                }
                                HttpStatusCode.Conflict to {
                                    description = "Email уже занят другим пользователем"
                                    body<ErrorResponseDto>()
                                }
                                HttpStatusCode.Forbidden to {
                                    description = "Недостаточно прав"
                                    body<ErrorResponseDto>()
                                }
                            }
                        }) {
                            call.requireRole(RoleType.ADMINISTRATOR)
                            val id = call.uuidParam("id")

                            if (call.callerId() == id) {
                                throw AppError.Forbidden("You cannot update your own profile through this endpoint")
                            }

                            val dto = call.receive<UpdateUserRequestDto>()
                            val user = updateUserUseCase(id, dto)
                            call.respond(HttpStatusCode.OK, user.toResponseDto())
                        }

                        post("/block", {
                            tags("Users")
                            summary = "Block/unblock user"
                            description = "Устанавливает статус блокировки. " +
                                    "Заблокированный пользователь не может войти в систему. " +
                                    "**Только для администратора.**"
                            request {
                                pathParameter<String>("id") {
                                    description = "UUID пользователя"
                                }
                                body<BlockUserRequestDto> {
                                    description = "Статус блокировки"
                                    example("Заблокировать") {
                                        value = BlockUserRequestDto(isBlocked = true)
                                    }
                                    example("Разблокировать") {
                                        value = BlockUserRequestDto(isBlocked = false)
                                    }
                                }
                            }
                            response {
                                HttpStatusCode.OK to {
                                    description = "Обновлённый пользователь"
                                    body<UserResponseDto>()
                                }
                                HttpStatusCode.NotFound to {
                                    description = "Пользователь не найден"
                                    body<ErrorResponseDto>()
                                }
                                HttpStatusCode.Forbidden to {
                                    description = "Недостаточно прав"
                                    body<ErrorResponseDto>()
                                }
                            }
                        }) {
                            call.requireRole(RoleType.ADMINISTRATOR)
                            val id = call.uuidParam("id")
                            val dto = call.receive<BlockUserRequestDto>()
                            val user = blockUserUseCase(id, dto.isBlocked)
                            call.respond(HttpStatusCode.OK, user.toResponseDto())
                        }
                    }
                }
            }
        }
    }
}