package ua.com.goit.clearbreath.analysis.security

import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class BearerTokenServerAuthenticationConverter : ServerAuthenticationConverter {
    override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
        val authHeader = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION) ?: return Mono.empty()
        if (!authHeader.startsWith("Bearer ", ignoreCase = true)) return Mono.empty()

        val token = authHeader.substringAfter("Bearer ").trim()
        if (token.isBlank()) return Mono.empty()

        return Mono.just(UsernamePasswordAuthenticationToken("jwt", token))
    }
}