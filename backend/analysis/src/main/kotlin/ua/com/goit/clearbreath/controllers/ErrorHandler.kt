package ua.com.goit.clearbreath.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import ua.com.goit.clearbreath.model.ErrorResponse

@ControllerAdvice
class ErrorHandler {

    @ExceptionHandler
    fun handle(ex: Exception): ResponseEntity<ErrorResponse> {
        ex.printStackTrace()
        return ResponseEntity.status(500)
            .body(ErrorResponse( "500", ex.message ?: "Internal Server Error"))
    }

}