package ua.com.goit.clearbreath.analysis.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import ua.com.goit.clearbreath.analysis.api.AuthenticationApi
import ua.com.goit.clearbreath.analysis.domain.exceptions.AuthenticationException
import ua.com.goit.clearbreath.analysis.domain.services.TokenService
import ua.com.goit.clearbreath.analysis.domain.services.UserService
import ua.com.goit.clearbreath.analysis.model.TokenResponse
import ua.com.goit.clearbreath.analysis.model.UserLoginRequest
import ua.com.goit.clearbreath.analysis.utils.PasswordHasher.verifyPassword

@RestController
class AuthenticationController(
    private val tokenService: TokenService,
    private val userService: UserService
) : AuthenticationApi {

    override suspend fun loginUser(userLoginRequest: UserLoginRequest): ResponseEntity<TokenResponse> {
        return userService.findUserByEmail(userLoginRequest.email)
            .let { user ->
                val valid = verifyPassword(userLoginRequest.password, user.passwordHash)
                if (!valid) throw AuthenticationException("Invalid credentials")

                val token = tokenService.generateToken(user.email)
                ResponseEntity.ok(TokenResponse(token = token))
            }
            ?: throw AuthenticationException("Login timeout or empty result")
    }
}
