package ua.com.goit.clearbreath.analysis.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import ua.com.goit.clearbreath.analysis.domain.services.TokenService
import ua.com.goit.clearbreath.analysis.security.BearerTokenServerAuthenticationConverter
import ua.com.goit.clearbreath.analysis.security.JwtReactiveAuthenticationManager

@Configuration
@EnableReactiveMethodSecurity
class SecurityConfig(
    private val tokenService: TokenService
) {

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        val authWebFilter = AuthenticationWebFilter(JwtReactiveAuthenticationManager(tokenService)).apply {
            setServerAuthenticationConverter(BearerTokenServerAuthenticationConverter())
            // Stateless: контекст не зберігаємо в сесії, тільки на запит
            setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance())
        }

        return http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .authorizeExchange {
                it.pathMatchers("/v1/auth/**", "/v1/users/register", "/actuator/health").permitAll()
                    .anyExchange().authenticated()
            }
            .addFilterAt(authWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build()
    }
}