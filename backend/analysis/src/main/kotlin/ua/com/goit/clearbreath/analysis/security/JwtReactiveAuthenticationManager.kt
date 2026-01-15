package ua.com.goit.clearbreath.analysis.security

import kotlinx.coroutines.reactor.mono
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import reactor.core.publisher.Mono
import ua.com.goit.clearbreath.analysis.domain.services.TokenService
import ua.com.goit.clearbreath.analysis.domain.services.UserService

class JwtReactiveAuthenticationManager(
    private val tokenService: TokenService,
    private val userService: UserService
) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        val token = authentication.credentials?.toString() ?: return Mono.empty()

        return mono {
            val claims = tokenService.verifyAndGetClaims(token)
            val email = claims.subject ?: error("Missing subject")

            val user = userService.findUserByEmail(email)
            JwtAuthentication(user, emptyList())
        }
    }
}
