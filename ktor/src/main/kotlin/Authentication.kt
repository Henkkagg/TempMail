import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpHeaders
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.http.auth.parseAuthorizationHeader
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.auth.principal
import java.util.Date

private val secret = System.getenv("JWT_SECRET")

fun Application.configureAuthentication() {

    install(Authentication) {
        jwt {
            authHeader { call ->
                val token = call.request.cookies["token"] ?: return@authHeader null
                HttpAuthHeader.Single("Bearer", token)
            }
            verifier(
                JWT
                    .require(Algorithm.HMAC256(secret))
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("email").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}

fun ApplicationCall.getEmailIfValidJWT(): String? {

    val token = this.request.cookies["token"] ?: return null

    //To throw TokenExpiredException if the token is either expired or invalid
    return runCatching {
        val decoded = JWT.decode(token)
        JWT.require(Algorithm.HMAC256(secret)).build().verify(decoded)
        decoded.getClaim("email").asString()
    }.getOrNull()

}

fun createToken(email: String): String {

    return JWT.create()
        .withClaim("email", email)
        .withExpiresAt(Date(System.currentTimeMillis() + 600000))
        .sign(Algorithm.HMAC256(System.getenv("JWT_SECRET")))
}