package com.noizy.interfaces.rest

import com.noizy.application.service.AuthService
import com.noizy.application.service.UserProfileService
import com.noizy.infrastructure.security.UserPrincipal
import com.noizy.interfaces.dto.UserResponse
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserController(
    private val authService: AuthService,
    private val userProfileService: UserProfileService
) {
    @GetMapping("/me")
    fun me(@AuthenticationPrincipal principal: UserPrincipal): UserResponse =
        authService.me(principal.id)

    @PostMapping("/me/avatar")
    fun uploadAvatar(
        @RequestPart("avatar") avatar: MultipartFile,
        @AuthenticationPrincipal principal: UserPrincipal
    ): UserResponse =
        userProfileService.uploadAvatar(principal.id, avatar)

    @GetMapping("/{id}/avatar")
    fun avatar(@PathVariable id: UUID): ResponseEntity<InputStreamResource> {
        val result = userProfileService.avatar(id)
        val headers = HttpHeaders()
        headers.contentType = MediaType.parseMediaType(result.contentType)
        headers.contentLength = result.contentLength
        return ResponseEntity(InputStreamResource(result.content), headers, HttpStatus.OK)
    }
}
