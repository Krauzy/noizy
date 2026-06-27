package com.noizy.domain.exception

open class NoizyException(message: String) : RuntimeException(message)

class NotFoundException(resource: String) : NoizyException("$resource not found")

class ConflictException(message: String) : NoizyException(message)

class ForbiddenException(message: String = "Forbidden") : NoizyException(message)

class UnauthorizedException(message: String = "Unauthorized") : NoizyException(message)

class BadRequestException(message: String) : NoizyException(message)
