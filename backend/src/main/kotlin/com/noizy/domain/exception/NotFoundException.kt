package com.noizy.domain.exception

class NotFoundException(resource: String) : NoizyException("$resource not found")
