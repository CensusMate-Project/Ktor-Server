package org.censusmate.di

import io.ktor.server.application.Application
import io.ktor.server.application.log
import org.censusmate.controller.AuthController
import org.censusmate.controller.UserController
import org.censusmate.data.repository.UserRepositoryImpl
import org.censusmate.domain.repository.UserRepository
import org.censusmate.domain.usecase.auth.GetMeUseCase
import org.censusmate.domain.usecase.auth.LoginUseCase
import kotlin.getValue

object AppContainer {
    val userRepository: UserRepository by lazy { UserRepositoryImpl() }

    val loginUseCase: LoginUseCase by lazy { LoginUseCase(userRepository) }
    val getMeUseCase: GetMeUseCase by lazy { GetMeUseCase(userRepository) }

    val authController: AuthController by lazy { AuthController(loginUseCase) }
    val userController: UserController by lazy { UserController(getMeUseCase) }
}

fun Application.appModule() {
    log.info("DI container initialized")
}