import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.gzmo.service.dao
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText
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
                val priceForRange = dao.priceForRange(start, end)
                if (priceForRange == null) call.respondText("unavailable", status = HttpStatusCode.NotFound)
                else call.respond(priceForRange)
            }
        }
    }
}