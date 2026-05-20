package org.censusmate.controller

import io.github.smiley4.ktoropenapi.*
import io.ktor.http.*
import io.ktor.server.auth.authenticate
import io.ktor.server.request.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import org.censusmate.data.dto.CreatePersonRequestDto
import org.censusmate.data.dto.PersonResponseDto
import org.censusmate.data.dto.PersonsResponseDto
import org.censusmate.data.dto.UpdatePersonRequestDto
import org.censusmate.data.mapper.toResponseDto
import org.censusmate.domain.usecase.persons.CreatePersonUseCase
import org.censusmate.domain.usecase.persons.DeletePersonUseCase
import org.censusmate.domain.usecase.persons.GetPersonUseCase
import org.censusmate.domain.usecase.persons.GetPersonsUseCase
import org.censusmate.domain.usecase.persons.UpdatePersonUseCase
import org.censusmate.plugins.ErrorResponseDto
import org.censusmate.utils.requirePrincipal
import org.censusmate.utils.uuidParam

class PersonController(
    private val getPersonsUseCase: GetPersonsUseCase,
    private val getPersonUseCase: GetPersonUseCase,
    private val createPersonUseCase: CreatePersonUseCase,
    private val updatePersonUseCase: UpdatePersonUseCase,
    private val deletePersonUseCase: DeletePersonUseCase
) {
    fun configure(route: Route) {
        route.apply {
            authenticate("auth-jwt") {
                route.route("/households/{householdId}/persons") {
                    get({
                        tags("Persons")
                        summary = "Get persons in household"
                        description = "Возвращает список жителей домохозяйства с пагинацией."
                        request {
                            pathParameter<String>("householdId") {
                                description = "UUID домохозяйства"
                                example("Пример UUID") { value = "550e8400-e29b-41d4-a716-446655440000" }
                            }
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
                                description = "Список жителей"
                                body<PersonsResponseDto>()
                            }
                            HttpStatusCode.NotFound to {
                                description = "Домохозяйство не найдено"
                                body<ErrorResponseDto>()
                            }
                        }
                    }) {
                        val householdId = call.uuidParam("householdId")
                        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                        val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
                        val (persons, pagination) = getPersonsUseCase(householdId, page, limit)
                        call.respond(
                            HttpStatusCode.OK, PersonsResponseDto(
                                persons = persons.map { it.toResponseDto() }, pagination = pagination.toResponseDto()
                            )
                        )
                    }

                    post({
                        tags("Persons")
                        summary = "Add person to household"
                        description = "Добавляет нового жителя в домохозяйство."
                        request {
                            pathParameter<String>("householdId") {
                                description = "UUID домохозяйства"
                                example("Пример UUID") { value = "550e8400-e29b-41d4-a716-446655440000" }
                            }
                            body<CreatePersonRequestDto> {
                                description = "Данные жителя"
                                example("Пример") {
                                    value = CreatePersonRequestDto(
                                        gender = "male",
                                        birthDate = "1985-03-20",
                                        citizenship = "Россия",
                                        nationality = "Русский",
                                        nativeLanguage = "Русский",
                                        speaksRussian = true,
                                        educationLevel = "higher",
                                        maritalStatus = "married",
                                        childrenCount = 2,
                                        relationToHousehold = "head",
                                        employmentStatus = "employed",
                                        incomeSources = listOf("salary")
                                    )
                                }
                            }
                        }
                        response {
                            HttpStatusCode.Created to {
                                description = "Житель добавлен"
                                body<PersonResponseDto>()
                            }
                            HttpStatusCode.BadRequest to {
                                description = "Некорректные данные"
                                body<ErrorResponseDto>()
                            }
                            HttpStatusCode.NotFound to {
                                description = "Домохозяйство не найдено"
                                body<ErrorResponseDto>()
                            }
                        }
                    }) {
                        val householdId = call.uuidParam("householdId")
                        val dto = call.receive<CreatePersonRequestDto>()
                        val person = createPersonUseCase(householdId, dto)
                        call.respond(HttpStatusCode.Created, person.toResponseDto())
                    }
                }

                route.route("/persons/{personId}") {
                    get({
                        tags("Persons")
                        summary = "Get person by ID"
                        request {
                            pathParameter<String>("personId") {
                                description = "UUID жителя"
                                example("Пример UUID") { value = "550e8400-e29b-41d4-a716-446655440000" }
                            }
                        }
                        response {
                            HttpStatusCode.OK to {
                                description = "Данные жителя"
                                body<PersonResponseDto>()
                            }
                            HttpStatusCode.NotFound to {
                                description = "Житель не найден"
                                body<ErrorResponseDto>()
                            }
                        }
                    }) {
                        val id = call.uuidParam("personId")
                        val person = getPersonUseCase(id)
                        call.respond(HttpStatusCode.OK, person.toResponseDto())
                    }

                    put({
                        tags("Persons")
                        summary = "Update person"
                        description = "Обновляет данные жителя. Все поля опциональны."
                        request {
                            pathParameter<String>("personId") {
                                description = "UUID жителя"
                                example("Пример UUID") { value = "550e8400-e29b-41d4-a716-446655440000" }
                            }
                            body<UpdatePersonRequestDto> {
                                description = "Обновляемые поля"
                                example("Пример") {
                                    value = UpdatePersonRequestDto(
                                        employmentStatus = "retired", childrenCount = 3
                                    )
                                }
                            }
                        }
                        response {
                            HttpStatusCode.OK to {
                                description = "Житель обновлён"
                                body<PersonResponseDto>()
                            }
                            HttpStatusCode.NotFound to {
                                description = "Житель не найден"
                                body<ErrorResponseDto>()
                            }
                            HttpStatusCode.BadRequest to {
                                description = "Некорректные данные"
                                body<ErrorResponseDto>()
                            }
                        }
                    }) {
                        val principal = call.requirePrincipal()
                        val id = call.uuidParam("personId")
                        val dto = call.receive<UpdatePersonRequestDto>()
                        val person = updatePersonUseCase(id, dto, principal)
                        call.respond(HttpStatusCode.OK, person.toResponseDto())
                    }

                    delete({
                        tags("Persons")
                        summary = "Delete person"
                        description = "Удаляет жителя. Агент может удалить только жителя своего домохозяйства."
                        request {
                            pathParameter<String>("personId") {
                                description = "UUID жителя"
                                example("Пример UUID") { value = "550e8400-e29b-41d4-a716-446655440000" }
                            }
                        }
                        response {
                            HttpStatusCode.NoContent to {
                                description = "Житель удалён"
                            }
                            HttpStatusCode.NotFound to {
                                description = "Житель не найден"
                                body<ErrorResponseDto>()
                            }
                            HttpStatusCode.Forbidden to {
                                description = "Нет доступа"
                                body<ErrorResponseDto>()
                            }
                        }
                    }) {
                        val principal = call.requirePrincipal()
                        val id = call.uuidParam("personId")
                        deletePersonUseCase(id, principal)
                        call.respond(HttpStatusCode.NoContent)
                    }
                }
            }
        }
    }
}