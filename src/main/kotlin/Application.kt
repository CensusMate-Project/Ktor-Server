package org.censusmate

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.config.AuthScheme
import io.github.smiley4.ktoropenapi.config.AuthType
import io.github.smiley4.ktoropenapi.config.ExampleEncoder
import io.github.smiley4.ktoropenapi.config.SchemaGenerator
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.censusmate.data.database.DatabaseFactory
import org.censusmate.data.database.createDefaultAdminIfNotExists
import org.censusmate.di.appModule
import org.censusmate.plugins.configureAuthentication
import org.censusmate.plugins.configureCORS
import org.censusmate.plugins.configureCallLogging
import org.censusmate.plugins.configureContentNegotiation
import org.censusmate.plugins.configureStatusPages
import org.censusmate.plugins.json
import org.censusmate.routing.configureRouting
import org.censusmate.security.JwtConfig

fun main() {
    val config = Config.load()

    embeddedServer(
        Netty,
        port = config.server.port,
        host = "127.0.0.1"
    ) {
        module(config)
    }.start(wait = true)
}

fun Application.module(config: Config = Config.load()) {
    JwtConfig.init(config.jwt)

    install(OpenApi) {
        schemas {
            generator = SchemaGenerator.kotlinx(json)
        }
        examples {
            exampleEncoder = ExampleEncoder.kotlinx(json)
        }
        info {
            title = "Census API"
            version = "dev-0.0.1"
        }
        server {
            url = "http://localhost:3000"
        }
        security {
            securityScheme("BearerAuth") {
                type = AuthType.HTTP
                scheme = AuthScheme.BEARER
                bearerFormat = "JWT"
            }
        }
        pathFilter = { _, url ->
            url.firstOrNull() == "api"
        }
    }

    DatabaseFactory.init(this, config.database)
    createDefaultAdminIfNotExists()

    appModule()
    configureContentNegotiation()
    configureCallLogging()
    configureStatusPages()
    configureCORS()
    configureAuthentication()
    configureRouting()
}