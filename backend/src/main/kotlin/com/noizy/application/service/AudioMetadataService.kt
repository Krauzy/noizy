package com.noizy.application.service

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedInputStream
import javax.sound.sampled.AudioSystem

@Service
class AudioMetadataService {
    fun durationSeconds(file: MultipartFile): Int {
        return try {
            BufferedInputStream(file.inputStream).use { input ->
                AudioSystem.getAudioInputStream(input).use { audio ->
                    val frameRate = audio.format.frameRate
                    val frameLength = audio.frameLength
                    if (frameRate > 0 && frameLength > 0) {
                        (frameLength / frameRate).toInt().coerceAtLeast(0)
                    } else {
                        0
                    }
                }
            }
        } catch (_: Exception) {
            estimateMp3Duration(file)
        }
    }

    private fun estimateMp3Duration(file: MultipartFile): Int {
        return try {
            val bytes = file.bytes
            val start = skipId3(bytes)
            val headerIndex = (start until bytes.size - 3).firstOrNull { index ->
                (bytes[index].toInt() and 0xFF) == 0xFF && (bytes[index + 1].toInt() and 0xE0) == 0xE0
            } ?: return 0
            val bitrateKbps = mp3BitrateKbps(bytes, headerIndex)
            if (bitrateKbps <= 0) return 0
            (((bytes.size - start).toDouble() * 8) / (bitrateKbps * 1000)).toInt().coerceAtLeast(0)
        } catch (_: Exception) {
            0
        }
    }

    private fun skipId3(bytes: ByteArray): Int {
        if (bytes.size < 10 || bytes[0].toInt().toChar() != 'I' || bytes[1].toInt().toChar() != 'D' || bytes[2].toInt().toChar() != '3') {
            return 0
        }

        return 10 +
            ((bytes[6].toInt() and 0x7F) shl 21) +
            ((bytes[7].toInt() and 0x7F) shl 14) +
            ((bytes[8].toInt() and 0x7F) shl 7) +
            (bytes[9].toInt() and 0x7F)
    }

    private fun mp3BitrateKbps(bytes: ByteArray, index: Int): Int {
        val second = bytes[index + 1].toInt() and 0xFF
        val third = bytes[index + 2].toInt() and 0xFF
        val version = (second shr 3) and 0x03
        val layer = (second shr 1) and 0x03
        val bitrateIndex = (third shr 4) and 0x0F
        if (version == 1 || layer == 0 || bitrateIndex == 0 || bitrateIndex == 15) return 0

        val mpeg1Layer3 = intArrayOf(0, 32, 40, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320, 0)
        val mpeg2Layer3 = intArrayOf(0, 8, 16, 24, 32, 40, 48, 56, 64, 80, 96, 112, 128, 144, 160, 0)
        return if (version == 3) mpeg1Layer3[bitrateIndex] else mpeg2Layer3[bitrateIndex]
    }
}
