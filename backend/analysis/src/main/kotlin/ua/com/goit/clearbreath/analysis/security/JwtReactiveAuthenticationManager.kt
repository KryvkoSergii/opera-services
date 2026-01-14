package ua.com.goit.clearbreath.analysis.security

import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import reactor.core.publisher.Mono
import ua.com.goit.clearbreath.analysis.domain.services.TokenService

class JwtReactiveAuthenticationManager(
    private val tokenService: TokenService
) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        val token = authentication.credentials?.toString() ?: return Mono.empty()

        return Mono.fromCallable {
            val claims = tokenService.verifyAndGetClaims(token)
            val email = claims.subject ?: throw IllegalArgumentException("Missing subject")
            JwtAuthentication(email, emptyList())
        }
    }
}
