package com.noizy.application.dto

import java.io.InputStream

data class TrackCoverResult(
    val content: InputStream,
    val contentType: String,
    val contentLength: Long
)
