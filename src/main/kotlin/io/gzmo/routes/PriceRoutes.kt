import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.gzmo.dao.dao
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import java.time.ZonedDateTime

fun Application.priceRoutes() {
    routing {
        route("/price") {
            get {
                val start = ZonedDateTime.parse(call.request.queryParameters["start"])
                val end = ZonedDateTime.parse(call.request.queryParameters["end"])
                call.respond(dao.priceForRange(start, end))
            }
        }
    }
}