package org.censusmate.controller

import io.github.smiley4.ktoropenapi.*
import io.ktor.http.*
import io.ktor.server.auth.authenticate
import io.ktor.server.request.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import org.censusmate.data.dto.CreateHouseholdRequestDto
import org.censusmate.data.dto.HouseholdResponseDto
import org.censusmate.data.dto.HouseholdsResponseDto
import org.censusmate.data.dto.UpdateHouseholdRequestDto
import org.censusmate.data.mapper.toResponseDto
import org.censusmate.domain.usecase.household.CreateHouseholdUseCase
import org.censusmate.domain.usecase.household.DeleteHouseholdUseCase
import org.censusmate.domain.usecase.household.GetHouseholdUseCase
import org.censusmate.domain.usecase.household.GetHouseholdsUseCase
import org.censusmate.domain.usecase.household.UpdateHouseholdUseCase
import org.censusmate.plugins.ErrorResponseDto
import org.censusmate.utils.requirePrincipal
import org.censusmate.utils.uuidParam

class HouseholdController(
    private val getHouseholdsUseCase: GetHouseholdsUseCase,
    private val getHouseholdUseCase: GetHouseholdUseCase,
    private val createHouseholdUseCase: CreateHouseholdUseCase,
    private val updateHouseholdUseCase: UpdateHouseholdUseCase,
    private val deleteHouseholdUseCase: DeleteHouseholdUseCase
) {
    fun configure(route: Route) {
        route.apply {
            authenticate("auth-jwt") {
                route.route("/households") {
                    get({
                        tags("Households")
                        summary = "Get households list"
                        description = "Администратор видит все домохозяйства. Агент - только свои."
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
                                description = "Список домохозяйств"
                                body<HouseholdsResponseDto>()
                            }
                            HttpStatusCode.Unauthorized to {
                                description = "Не авторизован"
                                body<ErrorResponseDto>()
                            }
                        }
                    }) {
                        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                        val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
                        val (households, pagination) = getHouseholdsUseCase(page, limit)
                        call.respond(
                            HttpStatusCode.OK, HouseholdsResponseDto(
                                households = households.map { it.toResponseDto() },
                                pagination = pagination.toResponseDto()
                            )
                        )
                    }

                    post({
                        tags("Households")
                        summary = "Create household"
                        description =
                            "Создаёт новое домохозяйство. Привязывается к текущему агенту и активному событию переписи. Адрес валидируется через DaData."
                        request {
                            body<CreateHouseholdRequestDto> {
                                description = "Данные домохозяйства"
                                example("Пример") {
                                    value = CreateHouseholdRequestDto(
                                        address = "г Москва, ул Ленина, д 1, кв 5",
                                        totalResidents = 3,
                                        dwellingType = "apartment",
                                        buildingYear = "1985",
                                        totalArea = 72,
                                        livingArea = 45,
                                        roomsCount = 3,
                                        notes = null
                                    )
                                }
                            }
                        }
                        response {
                            HttpStatusCode.Created to {
                                description = "Домохозяйство успешно создано"
                                body<HouseholdResponseDto>()
                            }
                            HttpStatusCode.BadRequest to {
                                description = "Некорректный адрес или нет активного события переписи"
                                body<ErrorResponseDto>()
                            }
                            HttpStatusCode.Unauthorized to {
                                description = "Не авторизован"
                                body<ErrorResponseDto>()
                            }
                        }
                    }) {
                        val principal = call.requirePrincipal()
                        val dto = call.receive<CreateHouseholdRequestDto>()
                        val household = createHouseholdUseCase(dto, principal)
                        call.respond(HttpStatusCode.Created, household.toResponseDto())
                    }

                    route("/{id}") {
                        get({
                            tags("Households")
                            summary = "Get household by ID"
                            description = "Агент может получить только своё домохозяйство."
                            request {
                                pathParameter<String>("id") {
                                    description = "UUID домохозяйства"
                                    example("Пример UUID") { value = "550e8400-e29b-41d4-a716-446655440000" }
                                }
                            }
                            response {
                                HttpStatusCode.OK to {
                                    description = "Данные домохозяйства"
                                    body<HouseholdResponseDto>()
                                }
                                HttpStatusCode.NotFound to {
                                    description = "Домохозяйство не найдено"
                                    body<ErrorResponseDto>()
                                }
                                HttpStatusCode.Forbidden to {
                                    description = "Нет доступа к чужому домохозяйству"
                                    body<ErrorResponseDto>()
                                }
                            }
                        }) {
                            val id = call.uuidParam("id")
                            val household = getHouseholdUseCase(id)
                            call.respond(HttpStatusCode.OK, household.toResponseDto())
                        }

                        put({
                            tags("Households")
                            summary = "Update household"
                            description =
                                "Агент может обновить только своё домохозяйство. Если передан адрес - валидируется через DaData."
                            request {
                                pathParameter<String>("id") {
                                    description = "UUID домохозяйства"
                                }
                                body<UpdateHouseholdRequestDto> {
                                    description = "Обновляемые поля (все опциональны)"
                                    example("Пример") {
                                        value = UpdateHouseholdRequestDto(
                                            totalResidents = 4,
                                            notes = "Обновлено после повторного обхода"
                                        )
                                    }
                                }
                            }
                            response {
                                HttpStatusCode.OK to {
                                    description = "Домохозяйство обновлено"
                                    body<HouseholdResponseDto>()
                                }
                                HttpStatusCode.NotFound to {
                                    description = "Домохозяйство не найдено"
                                    body<ErrorResponseDto>()
                                }
                                HttpStatusCode.Forbidden to {
                                    description = "Нет доступа к чужому домохозяйству"
                                    body<ErrorResponseDto>()
                                }
                                HttpStatusCode.BadRequest to {
                                    description = "Некорректный адрес"
                                    body<ErrorResponseDto>()
                                }
                            }
                        }) {
                            val principal = call.requirePrincipal()
                            val id = call.uuidParam("id")
                            val dto = call.receive<UpdateHouseholdRequestDto>()
                            val household = updateHouseholdUseCase(id, dto, principal)
                            call.respond(HttpStatusCode.OK, household.toResponseDto())
                        }

                        delete({
                            tags("Households")
                            summary = "Delete household"
                            description =
                                "Удаляет домохозяйство и всех жителей (CASCADE). Агент может удалить только своё."
                            request {
                                pathParameter<String>("id") {
                                    description = "UUID домохозяйства"
                                }
                            }
                            response {
                                HttpStatusCode.NoContent to {
                                    description = "Домохозяйство удалено"
                                }
                                HttpStatusCode.NotFound to {
                                    description = "Домохозяйство не найдено"
                                    body<ErrorResponseDto>()
                                }
                                HttpStatusCode.Forbidden to {
                                    description = "Нет доступа к чужому домохозяйству"
                                    body<ErrorResponseDto>()
                                }
                            }
                        }) {
                            val principal = call.requirePrincipal()
                            val id = call.uuidParam("id")
                            deleteHouseholdUseCase(id, principal)
                            call.respond(HttpStatusCode.NoContent)
                        }
                    }
                }
            }
        }
    }
}