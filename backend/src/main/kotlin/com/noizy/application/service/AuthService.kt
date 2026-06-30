package com.noizy.application.service

import com.noizy.domain.event.NoizyEventFactory
import com.noizy.domain.exception.ConflictException
import com.noizy.domain.exception.NotFoundException
import com.noizy.domain.exception.UnauthorizedException
import com.noizy.domain.model.UserEntity
import com.noizy.domain.model.UserRole
import com.noizy.application.dto.AuthResponse
import com.noizy.application.dto.LoginRequest
import com.noizy.application.dto.RegisterRequest
import com.noizy.application.dto.UserResponse
import com.noizy.application.mapper.toResponse
import com.noizy.application.port.input.AuthUseCase
import com.noizy.application.port.output.DomainEventPublisher
import com.noizy.application.port.output.TokenProvider
import com.noizy.application.port.output.persistence.UserRepositoryPort
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AuthService(
    private val users: UserRepositoryPort,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: TokenProvider,
    private val eventPublisher: DomainEventPublisher
) : AuthUseCase {
    @Transactional
    override fun register(request: RegisterRequest): AuthResponse {
        val email = request.email.trim().lowercase()
        if (users.existsByEmail(email)) {
            throw ConflictException("E-mail already registered")
        }
        val user = users.save(
            UserEntity(
                name = request.name.trim(),
                email = email,
                passwordHash = passwordEncoder.encode(request.password),
                role = UserRole.FREE_TIER
            )
        )
        eventPublisher.publish(NoizyEventFactory.userRegistered(user.id))
        return AuthResponse(jwtService.generateToken(user), user.toResponse())
    }

    @Transactional(readOnly = true)
    override fun login(request: LoginRequest): AuthResponse {
        val user = users.findByEmail(request.email.trim().lowercase())
            .orElseThrow { UnauthorizedException("Invalid credentials") }
        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            throw UnauthorizedException("Invalid credentials")
        }
        return AuthResponse(jwtService.generateToken(user), user.toResponse())
    }

    @Transactional(readOnly = true)
    override fun me(userId: UUID): UserResponse =
        users.findById(userId).orElseThrow { NotFoundException("User") }.toResponse()
}
