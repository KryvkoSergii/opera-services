package ua.com.goit.clearbreath.analysis.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import ua.com.goit.clearbreath.analysis.domain.services.TokenService
import ua.com.goit.clearbreath.analysis.domain.services.UserService
import ua.com.goit.clearbreath.analysis.security.BearerTokenServerAuthenticationConverter
import ua.com.goit.clearbreath.analysis.security.JwtReactiveAuthenticationManager

@Configuration
@EnableReactiveMethodSecurity
class SecurityConfig(
    private val tokenService: TokenService,
    private val userService: UserService
) {

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        val authWebFilter = AuthenticationWebFilter(JwtReactiveAuthenticationManager(tokenService, userService)).apply {
            setServerAuthenticationConverter(BearerTokenServerAuthenticationConverter())
            // Stateless: контекст не зберігаємо в сесії, тільки на запит
            setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance())
        }

        return http
            .cors { }
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .authorizeExchange {
                it.pathMatchers("/v1/auth/**", "/v1/users/register", "/actuator/health", "/v1/analyses/*/events").permitAll()
                    .anyExchange().authenticated()
            }
            .addFilterAt(authWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration()
        config.allowedOrigins = listOf("http://localhost:5173")
        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        config.allowedHeaders = listOf("*")
        config.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }
}