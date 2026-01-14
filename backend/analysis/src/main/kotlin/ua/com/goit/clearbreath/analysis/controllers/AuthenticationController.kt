package ua.com.goit.clearbreath.analysis.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import ua.com.goit.clearbreath.analysis.api.AuthenticationApi
import ua.com.goit.clearbreath.analysis.model.TokenResponse
import ua.com.goit.clearbreath.analysis.model.UserLoginRequest

@RestController
class AuthenticationController : AuthenticationApi {

    override fun loginUser(userLoginRequest: UserLoginRequest): ResponseEntity<TokenResponse> {
        return super.loginUser(userLoginRequest)
    }
}
