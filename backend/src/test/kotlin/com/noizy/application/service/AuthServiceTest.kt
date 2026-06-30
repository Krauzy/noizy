package com.noizy.application.service

import com.noizy.domain.model.UserEntity
import com.noizy.domain.model.UserRole
import com.noizy.application.dto.RegisterRequest
import com.noizy.application.port.output.DomainEventPublisher
import com.noizy.application.port.output.TokenProvider
import com.noizy.application.port.output.persistence.UserRepositoryPort
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.UUID

class AuthServiceTest {
    private val users = mockk<UserRepositoryPort>()
    private val encoder = mockk<PasswordEncoder>()
    private val jwt = mockk<TokenProvider>()
    private val events = mockk<DomainEventPublisher>()
    private val service = AuthService(users, encoder, jwt, events)

    @Test
    fun `register creates user with normalized email and token`() {
        every { users.existsByEmail("listener@noizy.local") } returns false
        every { encoder.encode("password123") } returns "hash"
        every { events.publish(any()) } just Runs
        every { jwt.generateToken(any()) } returns "jwt-token"
        every { users.save(any()) } answers {
            firstArg<UserEntity>().apply {
                id = UUID.fromString("11111111-1111-1111-1111-111111111111")
                role = UserRole.FREE_TIER
            }
        }

        val response = service.register(RegisterRequest(" Listener ", "Listener@Noizy.Local", "password123"))

        assertEquals("jwt-token", response.token)
        assertEquals("listener@noizy.local", response.user.email)
        assertEquals("Listener", response.user.name)
        verify { users.save(match { it.email == "listener@noizy.local" && it.passwordHash == "hash" }) }
        verify { events.publish(match { it.type.name == "USER_REGISTERED" }) }
    }

    @Test
    fun `register rejects duplicate email`() {
        every { users.existsByEmail("listener@noizy.local") } returns true

        assertThrows<RuntimeException> {
            service.register(RegisterRequest("Listener", "listener@noizy.local", "password123"))
        }
    }
}
