package ua.com.goit.clearbreath.analysis.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import ua.com.goit.clearbreath.analysis.api.UserApi
import ua.com.goit.clearbreath.analysis.domain.mapper.UserMapper
import ua.com.goit.clearbreath.analysis.domain.services.UserService
import ua.com.goit.clearbreath.analysis.model.UserRegisterRequest
import ua.com.goit.clearbreath.analysis.model.UserRegisterResponse

@RestController
class UserController(private val userService: UserService, private val userMapper: UserMapper) : UserApi {

    override suspend fun registerUser(userRegisterRequest: UserRegisterRequest): ResponseEntity<UserRegisterResponse> {

        val user = userService.createUser(userRegisterRequest.email,
            userRegisterRequest.password,
            userRegisterRequest.gender)

        val mapped = user.let { userMapper.toRegResponse(it) }

        return mapped.let { ResponseEntity.ok(it) }
    }

    override suspend fun getMeUserDetails(): ResponseEntity<UserRegisterResponse> {
        return super.getMeUserDetails()
    }
}

