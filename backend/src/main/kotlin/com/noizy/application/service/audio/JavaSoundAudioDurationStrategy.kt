package com.noizy.application.service.audio

import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedInputStream
import javax.sound.sampled.AudioSystem

@Component
@Order(0)
class JavaSoundAudioDurationStrategy : AudioDurationStrategy {
    override fun durationSeconds(file: MultipartFile): Int? =
        BufferedInputStream(file.inputStream).use { input ->
            AudioSystem.getAudioInputStream(input).use { audio ->
                val frameRate = audio.format.frameRate
                val frameLength = audio.frameLength
                if (frameRate > 0 && frameLength > 0) {
                    (frameLength / frameRate).toInt().coerceAtLeast(0)
                } else {
                    null
                }
            }
        }
}
