package org.censusmate.controller

import io.github.smiley4.ktoropenapi.*
import io.ktor.http.*
import io.ktor.server.auth.authenticate
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import org.censusmate.data.dto.LoginRequestDto
import org.censusmate.data.dto.MeResponseDto
import org.censusmate.data.dto.TokenResponseDto
import org.censusmate.data.mapper.toMeResponseDto
import org.censusmate.domain.usecase.auth.GetMeUseCase
import org.censusmate.domain.usecase.auth.LoginUseCase
import org.censusmate.plugins.ErrorResponse
import org.censusmate.utils.requirePrincipal

class AuthController(
    private val loginUseCase: LoginUseCase,
    private val getMeUseCase: GetMeUseCase
) {
    fun configure(route: Route) {
        route.apply {
            route("/auth") {
                post("/login", {
                    tags("Auth")
                    summary = "Login to the platform"
                    description = "Аутентификация пользователя и получение JWT токена для доступа к защищённым ресурсам"
                    request {
                        body<LoginRequestDto> {
                            description = "Учётные данные пользователя"
                            example("Пример") {
                                value = LoginRequestDto(
                                    email = "admin@census.ru",
                                    password = "admin123"
                                )
                            }
                        }
                    }
                    response {
                        HttpStatusCode.OK to {
                            description = "Успешная аутентификация"
                            body<TokenResponseDto>()
                        }
                        HttpStatusCode.Unauthorized to {
                            description = "Неверный email или пароль"
                            body<ErrorResponse>()
                        }
                        HttpStatusCode.Forbidden to {
                            description = "Аккаунт заблокирован администратором"
                            body<ErrorResponse>()
                        }
                    }
                }) {
                    val request = call.receive<LoginRequestDto>()
                    val (_, token) = loginUseCase(request.email, request.password)
                    call.respond(HttpStatusCode.OK, TokenResponseDto(token))
                }

                authenticate("auth-jwt") {
                    get("/me", {
                        tags("Auth")
                        summary = "Get current user info"
                        description = "Получить информацию о текущем пользователе"
                        response {
                            HttpStatusCode.OK to {
                                description = "Профиль текущего пользователя"
                                body<MeResponseDto>()
                            }
                            HttpStatusCode.Unauthorized to {
                                description = "Токен отсутствует или истёк"
                                body<ErrorResponse>()
                            }
                        }
                    }) {
                        val principal = call.requirePrincipal()
                        val user = getMeUseCase(principal.userId)
                        call.respond(HttpStatusCode.OK, user.toMeResponseDto())
                    }
                }
            }
        }
    }
}