package org.censusmate.controller

import io.github.smiley4.ktoropenapi.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.censusmate.data.dto.LoginRequestDto
import org.censusmate.data.dto.TokenResponseDto
import org.censusmate.domain.usecase.auth.LoginUseCase
import org.censusmate.plugins.ErrorResponse

class AuthController(private val loginUseCase: LoginUseCase) {
    fun configure(route: Route) {
        route.apply {
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
        }
    }
}