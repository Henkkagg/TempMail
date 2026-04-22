import di.appModule
import domain.DeleteOldEmails
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.sse.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.exposed.v1.jdbc.Database
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import routing.configureRouting
import javax.sql.DataSource
import kotlin.time.Duration.Companion.milliseconds

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
    install(SSE)
    install(ContentNegotiation) {
        gson()
    }

    Database.connect(get<DataSource>())
    configureAuthentication()
    configureRouting()

    CoroutineScope(Dispatchers.IO).launch {
        val deleteOldEmails: DeleteOldEmails by inject()
        while(true) {
            deleteOldEmails()
            delay(300000.milliseconds)
        }
    }
}
