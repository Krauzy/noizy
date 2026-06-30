package com.noizy.application.port.output

import java.io.InputStream

data class StoredObjectStream(
    val stream: InputStream,
    val contentType: String,
    val totalLength: Long,
    val returnedLength: Long
)
