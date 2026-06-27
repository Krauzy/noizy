package com.noizy.application.service

import com.noizy.domain.event.NoizyEvent
import com.noizy.domain.event.NoizyEventType
import com.noizy.domain.exception.ConflictException
import com.noizy.domain.exception.NotFoundException
import com.noizy.domain.exception.UnauthorizedException
import com.noizy.infrastructure.messaging.EventPublisher
import com.noizy.infrastructure.persistence.entity.UserEntity
import com.noizy.infrastructure.persistence.entity.UserRole
import com.noizy.infrastructure.persistence.repository.UserJpaRepository
import com.noizy.infrastructure.security.JwtService
import com.noizy.interfaces.dto.AuthResponse
import com.noizy.interfaces.dto.LoginRequest
import com.noizy.interfaces.dto.RegisterRequest
import com.noizy.interfaces.dto.UserResponse
import com.noizy.interfaces.mapper.toResponse
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AuthService(
    private val users: UserJpaRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val eventPublisher: EventPublisher
) {
    @Transactional
    fun register(request: RegisterRequest): AuthResponse {
        val email = request.email.trim().lowercase()
        if (users.existsByEmail(email)) {
            throw ConflictException("E-mail already registered")
        }
        val user = users.save(
            UserEntity(
                name = request.name.trim(),
                email = email,
                passwordHash = passwordEncoder.encode(request.password),
                role = UserRole.USER
            )
        )
        eventPublisher.publish(
            NoizyEvent(
                type = NoizyEventType.USER_REGISTERED,
                actorUserId = user.id,
                aggregateId = user.id
            )
        )
        return AuthResponse(jwtService.generateToken(user), user.toResponse())
    }

    @Transactional(readOnly = true)
    fun login(request: LoginRequest): AuthResponse {
        val user = users.findByEmail(request.email.trim().lowercase())
            .orElseThrow { UnauthorizedException("Invalid credentials") }
        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            throw UnauthorizedException("Invalid credentials")
        }
        return AuthResponse(jwtService.generateToken(user), user.toResponse())
    }

    @Transactional(readOnly = true)
    fun me(userId: UUID): UserResponse =
        users.findById(userId).orElseThrow { NotFoundException("User") }.toResponse()
}
