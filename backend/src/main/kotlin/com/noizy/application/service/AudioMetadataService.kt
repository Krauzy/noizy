package com.noizy.application.service

import com.noizy.application.service.audio.AudioDurationStrategy
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class AudioMetadataService(
    private val durationStrategies: List<AudioDurationStrategy>
) {
    fun durationSeconds(file: MultipartFile): Int =
        durationStrategies.firstNotNullOfOrNull { strategy ->
            runCatching { strategy.durationSeconds(file) }.getOrNull()
        }?.coerceAtLeast(0) ?: 0
}
