package com.noizy.application.service.audio

import org.springframework.web.multipart.MultipartFile

interface AudioDurationStrategy {
    fun durationSeconds(file: MultipartFile): Int?
}
