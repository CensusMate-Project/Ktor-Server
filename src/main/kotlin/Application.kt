package org.censusmate

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.config.AuthScheme
import io.github.smiley4.ktoropenapi.config.AuthType
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

fun main() {
    embeddedServer(Netty, port = 3000, host = "127.0.0.1") {
        module()
    }.start(wait = true)
}

fun Application.module() {
    install(OpenApi) {
        schemas {
            generator = SchemaGenerator.kotlinx(json)
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

    DatabaseFactory.init(this)
    createDefaultAdminIfNotExists()

    appModule()
    configureContentNegotiation()
    configureCallLogging()
    configureStatusPages()
    configureCORS()
    configureAuthentication()
    configureRouting()
}