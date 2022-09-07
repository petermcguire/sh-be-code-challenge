import io.gzmo.dao.dao
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.ratesRoutes() {
    routing {
        route("/rates") {
            get {
                call.respond(dao.allRates())
            }
            put {
                val rates = call.receive<AllRates>()
                call.respond(dao.updateRates(rates))
            }
        }
    }
}