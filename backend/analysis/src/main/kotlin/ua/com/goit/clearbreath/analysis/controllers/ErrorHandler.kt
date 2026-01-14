package ua.com.goit.clearbreath.analysis.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import ua.com.goit.clearbreath.analysis.domain.exceptions.UserExistsException

@ControllerAdvice
class ErrorHandler {

    @ExceptionHandler
    fun handle(ex: Exception): ResponseEntity<ErrorResponse> {
        ex.printStackTrace()
        return ResponseEntity.status(500)
            .body(ErrorResponse(ex.message ?: "Internal Server Error"))
    }

    @ExceptionHandler(UserExistsException::class)
    fun handleConflictException(ex: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(409).body(ErrorResponse(ex.message ?: "Conflict"))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIncorrectDataException(ex: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(400).body(ErrorResponse(ex.message ?: "Bad Request"))
    }



}