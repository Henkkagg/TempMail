package routing

import com.google.gson.Gson
import createToken
import domain.GenerateEmailAddress
import domain.SubscriptionService
import getEmailIfValidJWT
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sse.*
import io.ktor.sse.*
import kotlinx.coroutines.awaitCancellation
import org.koin.ktor.ext.inject
import kotlin.time.Duration.Companion.milliseconds

fun Application.configureRouting() {
    routing {

        staticResources("/", "static") {
            default("index.html")
        }

        route("/api") {

            sse("/events") {
                val address = call.getEmailIfValidJWT() ?: return@sse

                val subscriptionService: SubscriptionService by inject()

                //Needed so Ktor knows when the connection is dead, and we can unsubscribe
                heartbeat {
                    period = 1000.milliseconds
                    event = ServerSentEvent("heartbeat")
                }

                val subscriptionId = subscriptionService.subscribe(address) { email ->
                    if (email == null) {
                        send("no emails", "email")
                    } else {
                        send(Gson().toJson(email), "email")
                    }
                }

                //Need try...catch block because otherwise nothing would execute after awaitCancellation()
                try {
                    awaitCancellation()
                } finally {
                    subscriptionService.unsubscribe(address, subscriptionId)
                }
            }

            get("/token") {
                var address = call.getEmailIfValidJWT()

                if (address == null) {
                    val generateEmailAddress: GenerateEmailAddress by inject()
                    address = generateEmailAddress()

                    //All unique addresses exhausted
                    if (address == null) {
                        call.respond(HttpStatusCode.ServiceUnavailable)
                        return@get
                    }
                }
                val token = createToken(address)
                call.response.cookies.append("token", token, httpOnly = true)
                call.respond(mapOf("address" to address))
            }
        }
    }
}