package org.censusmate.controller

import io.github.smiley4.ktoropenapi.*
import io.ktor.http.*
import io.ktor.server.auth.authenticate
import io.ktor.server.request.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import org.censusmate.data.dto.AddressSuggestResponseDto
import org.censusmate.data.dto.AddressSuggestionDto
import org.censusmate.domain.usecase.suggest.SuggestAddressUseCase
import org.censusmate.plugins.ErrorResponseDto
import org.censusmate.utils.AppError

class AddressController(private val suggestAddressUseCase: SuggestAddressUseCase) {
    fun configure(route: Route) {
        route.apply {
            authenticate("auth-jwt") {
                route("/address") {
                    get("/suggest", {
                        tags("Address")
                        summary = "Suggest address"
                        description =
                            "Возвращает подсказки адресов через DaData. Используется на клиенте при вводе адреса."
                        request {
                            queryParameter<String>("query") {
                                description = "Поисковый запрос"
                                required = true
                                example("Пример") { value = "Москва Ленина" }
                            }
                            queryParameter<Int>("count") {
                                description = "Количество подсказок (по умолчанию 5)"
                                required = false
                            }
                        }
                        response {
                            HttpStatusCode.OK to {
                                description = "Список подсказок"
                                body<AddressSuggestResponseDto>()
                            }
                            HttpStatusCode.BadRequest to {
                                description = "Пустой или слишком короткий запрос"
                                body<ErrorResponseDto>()
                            }
                        }
                    }) {
                        val query = call.request.queryParameters["query"]
                            ?: throw AppError.BadRequest("query parameter is required")
                        val count = call.request.queryParameters["count"]?.toIntOrNull() ?: 5

                        val suggestions = suggestAddressUseCase(query, count)

                        call.respond(
                            HttpStatusCode.OK, AddressSuggestResponseDto(
                                suggestions = suggestions.map {
                                    AddressSuggestionDto(
                                        value = it.value,
                                        unrestrictedValue = it.unrestrictedValue
                                    )
                                }
                            ))
                    }
                }
            }
        }
    }
}