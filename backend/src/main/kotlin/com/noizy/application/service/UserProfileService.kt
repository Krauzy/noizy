package com.noizy.application.service

import com.noizy.domain.exception.BadRequestException
import com.noizy.domain.exception.NotFoundException
import com.noizy.infrastructure.aws.S3StorageService
import com.noizy.infrastructure.persistence.repository.UserJpaRepository
import com.noizy.interfaces.dto.UserAvatarResult
import com.noizy.interfaces.dto.UserResponse
import com.noizy.interfaces.mapper.toResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
class UserProfileService(
    private val users: UserJpaRepository,
    private val storage: S3StorageService
) {
    @Transactional
    fun uploadAvatar(userId: UUID, avatar: MultipartFile): UserResponse {
        if (avatar.isEmpty) throw BadRequestException("Avatar image is required")
        if (!avatar.contentType.orEmpty().startsWith("image/")) {
            throw BadRequestException("Avatar must be an image file")
        }

        val user = users.findById(userId).orElseThrow { NotFoundException("User") }
        user.avatarS3Key = storage.uploadAvatar(avatar)
        return user.toResponse()
    }

    @Transactional(readOnly = true)
    fun avatar(userId: UUID): UserAvatarResult {
        val user = users.findById(userId).orElseThrow { NotFoundException("User") }
        val key = user.avatarS3Key ?: throw NotFoundException("User avatar")
        val image = storage.getImage(key)
        return UserAvatarResult(
            content = image.stream,
            contentType = image.contentType,
            contentLength = image.contentLength
        )
    }
}
