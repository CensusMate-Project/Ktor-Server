package org.censusmate.controller

import io.github.smiley4.ktoropenapi.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.censusmate.data.dto.MeResponseDto
import org.censusmate.data.mapper.toMeResponseDto
import org.censusmate.domain.usecase.auth.GetMeUseCase
import org.censusmate.plugins.ErrorResponse
import org.censusmate.utils.requirePrincipal

class UserController(private val getMeUseCase: GetMeUseCase) {
    fun configure(route: Route) {
        route.apply {
            authenticate("auth-jwt") {
                get("/me", {
                    tags("User")
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