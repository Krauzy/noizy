package com.noizy.application.port.output.persistence

import com.noizy.domain.model.UserEntity
import java.util.Optional
import java.util.UUID

interface UserRepositoryPort {
    fun save(user: UserEntity): UserEntity
    fun findById(id: UUID): Optional<UserEntity>
    fun findByEmail(email: String): Optional<UserEntity>
    fun existsByEmail(email: String): Boolean
}
