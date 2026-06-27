package com.noizy.infrastructure.security

import com.noizy.infrastructure.persistence.repository.UserJpaRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val users: UserJpaRepository
) : OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val token = request.getHeader("Authorization")
            ?.takeIf { it.startsWith("Bearer ") }
            ?.removePrefix("Bearer ")

        if (!token.isNullOrBlank() && SecurityContextHolder.getContext().authentication == null) {
            runCatching {
                val claims = jwtService.parseClaims(token)
                val user = users.findByEmail(claims.subject).orElse(null)
                if (user?.id != null) {
                    val principal = UserPrincipal(user.id!!, user.email, user.role)
                    val authorities = listOf(SimpleGrantedAuthority("ROLE_${user.role.name}"))
                    SecurityContextHolder.getContext().authentication =
                        UsernamePasswordAuthenticationToken(principal, null, authorities)
                }
            }
        }

        filterChain.doFilter(request, response)
    }
}
