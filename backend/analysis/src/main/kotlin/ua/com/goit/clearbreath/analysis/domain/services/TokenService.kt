package ua.com.goit.clearbreath.analysis.domain.services

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ua.com.goit.clearbreath.analysis.domain.exceptions.AuthenticationException
import java.time.Instant
import java.util.*
import javax.crypto.SecretKey

@Component
class TokenService(
    @Value("\${auth.issuer}") private val issuer: String,
    @Value("\${auth.secret}") private val secret: String,
    private val clockSkewSeconds: Long = 30
) {

    private val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray(Charsets.UTF_8))

    fun generateToken(
        email: String,
        expiresInSeconds: Long = 3600,
        extraClaims: Map<String, Any> = emptyMap()
    ): String {
        val now = Instant.now()
        val exp = now.plusSeconds(expiresInSeconds)

        val builder = Jwts.builder()
            .issuer(issuer)
            .subject(email)
            .issuedAt(Date.from(now))
            .expiration(Date.from(exp))

        extraClaims.forEach { (k, v) -> builder.claim(k, v) }

        return builder.signWith(key).compact()
    }

    fun verifyAndGetClaims(token: String): Claims {
        val parser = Jwts.parser()
            .verifyWith(key)
            .requireIssuer(issuer)
            .clockSkewSeconds(clockSkewSeconds)
            .build()

        try {
            val jws = parser.parseSignedClaims(token)
            return jws.payload
        } catch (e: ExpiredJwtException) {
            throw AuthenticationException("Expired token")
        }
    }
}