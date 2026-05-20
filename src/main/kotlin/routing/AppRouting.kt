package org.censusmate.routing

import io.github.smiley4.ktoropenapi.openApi
import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.censusmate.di.AppContainer
import org.censusmate.plugins.scalarHtml

fun Application.configureRouting() {
    routing {
        route("api.json") {
            openApi()
        }
        get("/docs") {
            call.respondText(ContentType.Text.Html) {
                scalarHtml(specUrl = "/api.json", title = "Census API")
            }
        }
        get("/ping") {
            call.respondText("pong")
        }
        route("/api") {
            AppContainer.authController.configure(this)
            AppContainer.userController.configure(this)
            AppContainer.eventController.configure(this)
            AppContainer.addressController.configure(this)
            AppContainer.householdController.configure(this)
        }
    }
}