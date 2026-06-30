package com.noizy.application.port.output

import com.noizy.domain.model.UserEntity

interface TokenProvider {
    fun generateToken(user: UserEntity): String
}
