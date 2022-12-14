package io.gzmo

import DatabaseFactory
import io.ktor.server.application.Application
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import priceRoutes
import ratesRoutes

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    // set up content negotiation and serialization
    install(ContentNegotiation) {
        json()
    }
    // initialize database
    DatabaseFactory.init()
    // set up routes
    ratesRoutes()
    priceRoutes()
}
