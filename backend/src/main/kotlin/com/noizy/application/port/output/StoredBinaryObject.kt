package com.noizy.application.port.output

import java.io.InputStream

data class StoredBinaryObject(
    val stream: InputStream,
    val contentType: String,
    val contentLength: Long
)
