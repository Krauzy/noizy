package com.noizy.interfaces.rest

import com.noizy.domain.exception.BadRequestException
import com.noizy.domain.exception.ConflictException
import com.noizy.domain.exception.ForbiddenException
import com.noizy.domain.exception.NotFoundException
import com.noizy.domain.exception.UnauthorizedException
import com.noizy.interfaces.dto.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException::class)
    fun notFound(ex: NotFoundException, request: HttpServletRequest) =
        error(HttpStatus.NOT_FOUND, ex.message ?: "Not found", request)

    @ExceptionHandler(ConflictException::class)
    fun conflict(ex: ConflictException, request: HttpServletRequest) =
        error(HttpStatus.CONFLICT, ex.message ?: "Conflict", request)

    @ExceptionHandler(ForbiddenException::class)
    fun forbidden(ex: ForbiddenException, request: HttpServletRequest) =
        error(HttpStatus.FORBIDDEN, ex.message ?: "Forbidden", request)

    @ExceptionHandler(UnauthorizedException::class)
    fun unauthorized(ex: UnauthorizedException, request: HttpServletRequest) =
        error(HttpStatus.UNAUTHORIZED, ex.message ?: "Unauthorized", request)

    @ExceptionHandler(BadRequestException::class, ConstraintViolationException::class)
    fun badRequest(ex: Exception, request: HttpServletRequest) =
        error(HttpStatus.BAD_REQUEST, ex.message ?: "Bad request", request)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun validation(ex: MethodArgumentNotValidException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val message = ex.bindingResult.fieldErrors.joinToString("; ") { "${it.field}: ${it.defaultMessage}" }
        return error(HttpStatus.BAD_REQUEST, message.ifBlank { "Validation failed" }, request)
    }

    @ExceptionHandler(Exception::class)
    fun unexpected(ex: Exception, request: HttpServletRequest) =
        error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", request)

    private fun error(status: HttpStatus, message: String, request: HttpServletRequest): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(status).body(
            ErrorResponse(
                status = status.value(),
                error = status.reasonPhrase,
                message = message,
                path = request.requestURI
            )
        )
}
