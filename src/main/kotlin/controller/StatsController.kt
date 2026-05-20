package org.censusmate.controller

import io.github.smiley4.ktoropenapi.*
import io.ktor.http.*
import io.ktor.server.auth.authenticate
import io.ktor.server.request.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import org.censusmate.data.dto.StatsResponseDto
import org.censusmate.data.mapper.toResponseDto
import org.censusmate.domain.usecase.stats.GetStatsUseCase
import org.censusmate.plugins.ErrorResponseDto
import org.censusmate.utils.uuidParam

class StatsController(private val getStatsUseCase: GetStatsUseCase) {
    fun configure(route: Route) {
        route.route("/stats") {
            authenticate("auth-jwt") {
                get("/{id}", {
                    tags("Stats")
                    summary = "Get event statistics"
                    description = "Возвращает подробную статистику по событию переписи. **Только для администратора.**"
                    request {
                        pathParameter<String>("id") {
                            description = "UUID события переписи"
                            example("Пример") { value = "550e8400-e29b-41d4-a716-446655440000" }
                        }
                    }
                    response {
                        HttpStatusCode.OK to {
                            description = "Статистика события"
                            body<StatsResponseDto>()
                        }
                        HttpStatusCode.NotFound to {
                            description = "Событие не найдено"
                            body<ErrorResponseDto>()
                        }
                        HttpStatusCode.Forbidden to {
                            description = "Недостаточно прав"
                            body<ErrorResponseDto>()
                        }
                    }
                }) {
                    val eventId = call.uuidParam("id")
                    val stats = getStatsUseCase(eventId)
                    call.respond(HttpStatusCode.OK, stats.toResponseDto())
                }
            }
        }
    }
}