package org.censusmate.controller

import io.github.smiley4.ktoropenapi.*
import io.ktor.http.*
import io.ktor.server.auth.authenticate
import io.ktor.server.request.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import org.censusmate.data.dto.CreateEventRequestDto
import org.censusmate.data.dto.EventResponseDto
import org.censusmate.data.dto.EventsResponseDto
import org.censusmate.data.dto.UpdateEventRequestDto
import org.censusmate.data.mapper.toResponseDto
import org.censusmate.domain.model.RoleType
import org.censusmate.domain.usecase.event.CreateEventUseCase
import org.censusmate.domain.usecase.event.DeleteEventUseCase
import org.censusmate.domain.usecase.event.GetActiveEventsUseCase
import org.censusmate.domain.usecase.event.GetEventUseCase
import org.censusmate.domain.usecase.event.GetEventsUseCase
import org.censusmate.domain.usecase.event.UpdateEventUseCase
import org.censusmate.plugins.ErrorResponseDto
import org.censusmate.utils.callerRole
import org.censusmate.utils.requireRole
import org.censusmate.utils.uuidParam

class EventController(
    private val getEventsUseCase: GetEventsUseCase,
    private val getActiveEventsUseCase: GetActiveEventsUseCase,
    private val getEventUseCase: GetEventUseCase,
    private val createEventUseCase: CreateEventUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
    private val deleteEventUseCase: DeleteEventUseCase
) {
    fun configure(route: Route) {
        route.apply {
            authenticate("auth-jwt") {
                route("/events") {
                    get({
                        tags("Events")
                        summary = "All events list"
                        description = "Все переписи. Администратор видит все, агент - только активные."
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
                                description = "Список переписей"
                                body<EventsResponseDto>()
                            }
                        }
                    }) {
                        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                        val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
                        val (events, pagination) = if (call.callerRole() == RoleType.ADMINISTRATOR) {
                            getEventsUseCase(page, limit)
                        } else {
                            getActiveEventsUseCase(page, limit)
                        }
                        call.respond(
                            HttpStatusCode.OK, EventsResponseDto(
                                events = events.map { it.toResponseDto() },
                                pagination = pagination.toResponseDto()
                            )
                        )
                    }

                    post({
                        tags("Events")
                        summary = "Create new event"
                        description = "Создает новое событие. **Только для администратора.**"
                        request {
                            body<CreateEventRequestDto> {
                                description = "Данные для создания события"
                                example("Создать событие") {
                                    value = CreateEventRequestDto(
                                        name = "Перепись 2026",
                                        startDatetime = "2026-01-01T00:00:00Z",
                                        endDatetime = "2026-12-31T23:59:59Z",
                                    )
                                }
                            }
                        }
                        response {
                            HttpStatusCode.Created to {
                                description = "Событие успешно создано"
                                body<EventResponseDto>()
                            }
                            HttpStatusCode.Conflict to {
                                description = "Событие пересекается с существующим событием"
                                body<ErrorResponseDto>()
                            }
                            HttpStatusCode.Forbidden to {
                                description = "Недостаточно прав"
                                body<ErrorResponseDto>()
                            }
                        }
                    }) {
                        call.requireRole(RoleType.ADMINISTRATOR)
                        val dto = call.receive<CreateEventRequestDto>()
                        val event = createEventUseCase(dto)
                        call.respond(HttpStatusCode.Created, event.toResponseDto())
                    }

                    route("/{id}") {
                        get({
                            tags("Events")
                            summary = "Get event by ID"
                            description =
                                "Возвращает событие по UUID. Администратор видит все, агент - только активные."
                            request {
                                pathParameter<String>("id") {
                                    description = "UUID события"
                                    example("Пример UUID") { value = "550e8400-e29b-41d4-a716-446655440000" }
                                }
                            }
                            response {
                                HttpStatusCode.OK to {
                                    description = "Данные события"
                                    body<EventResponseDto>()
                                }
                                HttpStatusCode.NotFound to {
                                    description = "Событие не найдено или недоступно"
                                }
                                HttpStatusCode.Forbidden to {
                                    description = "Недостаточно прав"
                                    body<ErrorResponseDto>()
                                }
                            }
                        }) {
                            val id = call.uuidParam("id")
                            val event = getEventUseCase(id)
                            call.respond(HttpStatusCode.OK, event.toResponseDto())
                        }

                        put({
                            tags("Events")
                            summary = "Update event"
                            description = "Обновляет данные события. " +
                                    "Передавать только те поля, которые нужно изменить. " +
                                    "**Только для администратора.**"
                            request {
                                pathParameter<String>("id") {
                                    description = "UUID события"
                                    example("Пример UUID") { value = "550e8400-e29b-41d4-a716-446655440000" }
                                }
                                body<UpdateEventRequestDto> {
                                    description = "Данные для обновления события"
                                    example("Обновить название и даты") {
                                        value = UpdateEventRequestDto(
                                            name = "Перепись 2026 - обновленная",
                                            startDatetime = "2026-02-01T00:00:00Z",
                                            endDatetime = "2026-11-30T23:59:59Z",
                                        )
                                    }
                                }
                            }
                            response {
                                HttpStatusCode.OK to {
                                    description = "Событие обновлено"
                                    body<EventResponseDto>()
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
                            call.requireRole(RoleType.ADMINISTRATOR)
                            val id = call.uuidParam("id")
                            val dto = call.receive<UpdateEventRequestDto>()
                            val event = updateEventUseCase(id, dto)
                            call.respond(HttpStatusCode.OK, event.toResponseDto())
                        }

                        delete({
                            tags("Events")
                            summary = "Delete event"
                            description = "Удаляет событие. **Только для администратора.**"
                            request {
                                pathParameter<String>("id") {
                                    description = "UUID события"
                                    example("Пример UUID") { value = "550e8400-e29b-41d4-a716-446655440000" }
                                }
                            }
                            response {
                                HttpStatusCode.NoContent to {
                                    description = "Событие удалено"
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
                            call.requireRole(RoleType.ADMINISTRATOR)
                            val id = call.uuidParam("id")
                            deleteEventUseCase(id)
                            call.respond(HttpStatusCode.NoContent)
                        }
                    }
                }
            }
        }
    }
}