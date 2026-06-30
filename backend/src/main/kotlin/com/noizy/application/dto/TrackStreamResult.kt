package com.noizy.application.dto

import java.io.InputStream

data class TrackStreamResult(
    val content: InputStream,
    val contentType: String,
    val contentLength: Long,
    val statusCode: Int,
    val contentRange: String?
)
