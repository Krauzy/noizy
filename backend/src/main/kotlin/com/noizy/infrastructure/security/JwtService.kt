package com.noizy.infrastructure.security

import com.noizy.infrastructure.persistence.entity.UserEntity
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService(
    @Value("\${noizy.jwt.secret}") private val secret: String,
    @Value("\${noizy.jwt.expiration-minutes}") private val expirationMinutes: Long
) {
    private val signingKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray(Charsets.UTF_8))
    }

    fun generateToken(user: UserEntity): String {
        val now = Instant.now()
        val expiresAt = now.plusSeconds(expirationMinutes * 60)
        return Jwts.builder()
            .subject(user.email)
            .claim("uid", user.id.toString())
            .claim("role", user.role.name)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiresAt))
            .signWith(signingKey)
            .compact()
    }

    fun parseClaims(token: String): Claims =
        Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .payload
}
