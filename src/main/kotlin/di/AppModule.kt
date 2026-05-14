package org.censusmate.di

import io.ktor.server.application.Application
import io.ktor.server.application.log
import org.censusmate.controller.AuthController
import org.censusmate.controller.UserController
import org.censusmate.data.repository.UserRepositoryImpl
import org.censusmate.domain.repository.UserRepository
import org.censusmate.domain.usecase.auth.GetMeUseCase
import org.censusmate.domain.usecase.auth.LoginUseCase
import org.censusmate.domain.usecase.user.BlockUserUseCase
import org.censusmate.domain.usecase.user.CreateUserUseCase
import org.censusmate.domain.usecase.user.GetUserUseCase
import org.censusmate.domain.usecase.user.GetUsersUseCase
import org.censusmate.domain.usecase.user.UpdateUserUseCase
import kotlin.getValue

object AppContainer {
    val userRepository: UserRepository by lazy { UserRepositoryImpl() }

    val loginUseCase: LoginUseCase by lazy { LoginUseCase(userRepository) }
    val getMeUseCase: GetMeUseCase by lazy { GetMeUseCase(userRepository) }

    val blockUserUseCase: BlockUserUseCase by lazy { BlockUserUseCase(userRepository) }
    val createUserUseCase: CreateUserUseCase by lazy { CreateUserUseCase(userRepository) }
    val getUsersUseCase: GetUsersUseCase by lazy { GetUsersUseCase(userRepository) }
    val getUserUseCase: GetUserUseCase by lazy { GetUserUseCase(userRepository) }
    val updateUserUseCase: UpdateUserUseCase by lazy { UpdateUserUseCase(userRepository) }

    val authController: AuthController by lazy { AuthController(loginUseCase, getMeUseCase) }
    val userController: UserController by lazy {
        UserController(
            getUsersUseCase,
            getUserUseCase,
            createUserUseCase,
            updateUserUseCase,
            blockUserUseCase
        )
    }
}

fun Application.appModule() {
    log.info("DI container initialized")
}