package ua.com.goit.clearbreath.analysis.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import ua.com.goit.clearbreath.analysis.api.UserApi
import ua.com.goit.clearbreath.analysis.domain.mapper.UserMapper
import ua.com.goit.clearbreath.analysis.domain.services.UserService
import ua.com.goit.clearbreath.analysis.model.UserRegisterRequest
import ua.com.goit.clearbreath.analysis.model.UserRegisterResponse
import ua.com.goit.clearbreath.analysis.utils.Constants

@RestController
class UserController(private val userService: UserService, private val userMapper: UserMapper) : UserApi {

    override fun registerUser(userRegisterRequest: UserRegisterRequest): ResponseEntity<UserRegisterResponse> {

        val response = userService.createUser(userRegisterRequest.email,
            userRegisterRequest.password,
            userRegisterRequest.gender)

        val user = response.block(Constants.DURATION)
        val mapped = user?.let { userMapper.toRegResponse(it) }

        return mapped.let { ResponseEntity.ok(it) }
    }
}

