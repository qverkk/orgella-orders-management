package com.orgella.ordersmanagement.exceptions.handlers

import com.orgella.ordersmanagement.exceptions.NoOrderFoundException
import com.orgella.ordersmanagement.exceptions.NotEnoughItemsException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.util.stream.Collectors
import javax.validation.ConstraintViolationException

@ControllerAdvice
class CommonEndpointExceptionAdvisor : ResponseEntityExceptionHandler() {

    @ExceptionHandler(ConstraintViolationException::class)
    fun handle(
        exception: ConstraintViolationException,
        webRequest: WebRequest
    ): ResponseEntity<ConstraintViolationErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(
                ConstraintViolationErrorResponse(
                    "Validation failed for some classes",
                    exception.javaClass.simpleName,
                    exception.constraintViolations
                        .stream()
                        .map { ConstraintViolationError(it.message, it.propertyPath.toString()) }
                        .collect(Collectors.toList())
                )
            )
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val errors = ex.bindingResult
            .fieldErrors
            .stream()
            .map { ConstraintViolationError(it.defaultMessage ?: "No message", it.field) }
            .collect(Collectors.toList())

        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(
                ConstraintViolationErrorResponse(
                    "Invalid method arguments",
                    ex.javaClass.simpleName,
                    errors
                )
            )
    }

    @ExceptionHandler(
        NoOrderFoundException::class,
        NotEnoughItemsException::class
    )
    fun handleUnprocessable(exception: Exception): ResponseEntity<EndpointError> {
        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(EndpointError(exception.message ?: "No message", exception.javaClass.simpleName))
    }
}
