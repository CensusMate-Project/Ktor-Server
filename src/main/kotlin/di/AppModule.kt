package org.censusmate.di

import io.ktor.server.application.Application
import io.ktor.server.application.log
import org.censusmate.config.Config
import org.censusmate.controller.AddressController
import org.censusmate.controller.AuthController
import org.censusmate.controller.EventController
import org.censusmate.controller.HouseholdController
import org.censusmate.controller.UserController
import org.censusmate.data.remote.dadata.DaDataClient
import org.censusmate.data.repository.EventRepositoryImpl
import org.censusmate.data.repository.HouseholdRepositoryImpl
import org.censusmate.data.repository.UserRepositoryImpl
import org.censusmate.domain.repository.EventRepository
import org.censusmate.domain.repository.HouseholdRepository
import org.censusmate.domain.repository.UserRepository
import org.censusmate.domain.usecase.auth.GetMeUseCase
import org.censusmate.domain.usecase.auth.LoginUseCase
import org.censusmate.domain.usecase.event.CreateEventUseCase
import org.censusmate.domain.usecase.event.DeleteEventUseCase
import org.censusmate.domain.usecase.event.GetActiveEventsUseCase
import org.censusmate.domain.usecase.event.GetEventUseCase
import org.censusmate.domain.usecase.event.GetEventsUseCase
import org.censusmate.domain.usecase.event.UpdateEventUseCase
import org.censusmate.domain.usecase.household.CreateHouseholdUseCase
import org.censusmate.domain.usecase.household.DeleteHouseholdUseCase
import org.censusmate.domain.usecase.household.GetHouseholdUseCase
import org.censusmate.domain.usecase.household.GetHouseholdsUseCase
import org.censusmate.domain.usecase.household.UpdateHouseholdUseCase
import org.censusmate.domain.usecase.suggest.SuggestAddressUseCase
import org.censusmate.domain.usecase.user.BlockUserUseCase
import org.censusmate.domain.usecase.user.CreateUserUseCase
import org.censusmate.domain.usecase.user.GetUserUseCase
import org.censusmate.domain.usecase.user.GetUsersUseCase
import org.censusmate.domain.usecase.user.UpdateUserUseCase
import kotlin.getValue

object AppContainer {
    lateinit var config: Config
        private set

    val passwordMinLen get() = config.users.passwordMinLen

    val daDataClient by lazy { DaDataClient(config.dadata.apiKey, config.dadata.secretKey) }
    val suggestAddressUseCase by lazy { SuggestAddressUseCase(daDataClient) }

    val userRepository: UserRepository by lazy { UserRepositoryImpl() }
    val eventRepository: EventRepository by lazy { EventRepositoryImpl() }
    val householdRepository: HouseholdRepository by lazy { HouseholdRepositoryImpl() }

    val loginUseCase: LoginUseCase by lazy { LoginUseCase(userRepository) }
    val getMeUseCase: GetMeUseCase by lazy { GetMeUseCase(userRepository) }

    val blockUserUseCase: BlockUserUseCase by lazy { BlockUserUseCase(userRepository) }
    val createUserUseCase: CreateUserUseCase by lazy { CreateUserUseCase(userRepository) }
    val getUsersUseCase: GetUsersUseCase by lazy { GetUsersUseCase(userRepository) }
    val getUserUseCase: GetUserUseCase by lazy { GetUserUseCase(userRepository) }
    val updateUserUseCase: UpdateUserUseCase by lazy { UpdateUserUseCase(userRepository, passwordMinLen) }

    val getEventsUseCase: GetEventsUseCase by lazy { GetEventsUseCase(eventRepository) }
    val getActiveEventsUseCase: GetActiveEventsUseCase by lazy { GetActiveEventsUseCase(eventRepository) }
    val getEventUseCase: GetEventUseCase by lazy { GetEventUseCase(eventRepository) }
    val createEventUseCase: CreateEventUseCase by lazy { CreateEventUseCase(eventRepository) }
    val updateEventUseCase: UpdateEventUseCase by lazy { UpdateEventUseCase(eventRepository) }
    val deleteEventUseCase: DeleteEventUseCase by lazy { DeleteEventUseCase(eventRepository) }

    val getHouseholdsUseCase: GetHouseholdsUseCase by lazy { GetHouseholdsUseCase(householdRepository) }
    val getHouseholdUseCase: GetHouseholdUseCase by lazy { GetHouseholdUseCase(householdRepository) }
    val createHouseholdUseCase: CreateHouseholdUseCase by lazy {
        CreateHouseholdUseCase(
            householdRepository,
            eventRepository,
            suggestAddressUseCase
        )
    }
    val updateHouseholdUseCase: UpdateHouseholdUseCase by lazy {
        UpdateHouseholdUseCase(
            householdRepository,
            suggestAddressUseCase
        )
    }
    val deleteHouseholdUseCase: DeleteHouseholdUseCase by lazy { DeleteHouseholdUseCase(householdRepository) }

    val authController: AuthController by lazy { AuthController(loginUseCase, getMeUseCase) }
    val userController: UserController by lazy {
        UserController(getUsersUseCase, getUserUseCase, createUserUseCase, updateUserUseCase, blockUserUseCase)
    }
    val eventController: EventController by lazy {
        EventController(
            getEventsUseCase,
            getActiveEventsUseCase,
            getEventUseCase,
            createEventUseCase,
            updateEventUseCase,
            deleteEventUseCase
        )
    }
    val addressController: AddressController by lazy { AddressController(suggestAddressUseCase) }
    val householdController: HouseholdController by lazy {
        HouseholdController(
            getHouseholdsUseCase,
            getHouseholdUseCase,
            createHouseholdUseCase,
            updateHouseholdUseCase,
            deleteHouseholdUseCase
        )
    }

    fun init(config: Config) {
        this.config = config
    }
}

fun Application.appModule() {
    log.info("DI container initialized")
}