package org.censusmate.controller

import io.github.smiley4.ktoropenapi.*
import io.ktor.http.*
import io.ktor.server.auth.authenticate
import io.ktor.server.request.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import org.censusmate.data.dto.PersonResponseDto
import org.censusmate.data.dto.PersonsResponseDto
import org.censusmate.data.mapper.toResponseDto
import org.censusmate.domain.usecase.persons.CreatePersonUseCase
import org.censusmate.domain.usecase.persons.DeletePersonUseCase
import org.censusmate.domain.usecase.persons.GetPersonUseCase
import org.censusmate.domain.usecase.persons.GetPersonsUseCase
import org.censusmate.domain.usecase.persons.UpdatePersonUseCase
import org.censusmate.plugins.ErrorResponseDto
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
                                persons = persons.map { it.toResponseDto() },
                                pagination = pagination.toResponseDto()
                            )
                        )
                    }
                }
            }
        }
    }
}