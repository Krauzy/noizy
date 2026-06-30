package com.noizy.application.port.output

import org.springframework.web.multipart.MultipartFile

interface TrackStoragePort {
    fun uploadAudio(file: MultipartFile): String
    fun uploadCover(file: MultipartFile): String
    fun getAudio(key: String, rangeHeader: String?): StoredObjectStream
    fun getImage(key: String): StoredBinaryObject
}
