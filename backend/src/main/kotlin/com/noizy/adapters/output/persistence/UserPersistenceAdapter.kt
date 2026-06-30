package com.noizy.adapters.output.persistence

import com.noizy.adapters.output.persistence.repository.UserJpaRepository
import com.noizy.application.port.output.persistence.UserRepositoryPort
import com.noizy.domain.model.UserEntity
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
class UserPersistenceAdapter(
    private val repository: UserJpaRepository
) : UserRepositoryPort {
    override fun save(user: UserEntity): UserEntity = repository.save(user)
    override fun findById(id: UUID): Optional<UserEntity> = repository.findById(id)
    override fun findByEmail(email: String): Optional<UserEntity> = repository.findByEmail(email)
    override fun existsByEmail(email: String): Boolean = repository.existsByEmail(email)
}
