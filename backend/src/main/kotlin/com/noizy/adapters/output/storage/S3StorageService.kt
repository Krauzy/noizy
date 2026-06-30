package com.noizy.adapters.output.storage

import com.noizy.application.port.output.StoredBinaryObject
import com.noizy.application.port.output.StoredObjectStream
import com.noizy.application.port.output.TrackStoragePort
import com.noizy.domain.exception.NotFoundException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.HeadObjectRequest
import software.amazon.awssdk.services.s3.model.NoSuchKeyException
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.util.UUID

@Service
class S3StorageService(
    private val s3: S3Client,
    @Value("\${noizy.aws.s3.tracks-bucket}") private val tracksBucket: String,
    @Value("\${noizy.aws.s3.images-bucket}") private val imagesBucket: String
) : TrackStoragePort {
    override fun uploadAudio(file: MultipartFile): String {
        val extension = file.originalFilename?.substringAfterLast('.', "mp3") ?: "mp3"
        val key = "tracks/${UUID.randomUUID()}.$extension"
        put(tracksBucket, key, file)
        return key
    }

    override fun uploadCover(file: MultipartFile): String {
        val extension = file.originalFilename?.substringAfterLast('.', "jpg") ?: "jpg"
        val key = "covers/${UUID.randomUUID()}.$extension"
        put(imagesBucket, key, file)
        return key
    }

    override fun getAudio(key: String, rangeHeader: String?): StoredObjectStream {
        try {
            val head = s3.headObject(HeadObjectRequest.builder().bucket(tracksBucket).key(key).build())
            val builder = GetObjectRequest.builder().bucket(tracksBucket).key(key)
            if (!rangeHeader.isNullOrBlank()) {
                builder.range(rangeHeader)
            }
            val objectStream = s3.getObject(builder.build())
            return StoredObjectStream(
                stream = objectStream,
                contentType = objectStream.response().contentType() ?: "audio/mpeg",
                totalLength = head.contentLength(),
                returnedLength = objectStream.response().contentLength()
            )
        } catch (_: NoSuchKeyException) {
            throw NotFoundException("Audio object")
        }
    }

    override fun getImage(key: String): StoredBinaryObject {
        try {
            val objectStream = s3.getObject(GetObjectRequest.builder().bucket(imagesBucket).key(key).build())
            return StoredBinaryObject(
                stream = objectStream,
                contentType = objectStream.response().contentType() ?: "image/jpeg",
                contentLength = objectStream.response().contentLength()
            )
        } catch (_: NoSuchKeyException) {
            throw NotFoundException("Image object")
        }
    }

    private fun put(bucket: String, key: String, file: MultipartFile) {
        val request = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(file.contentType ?: "application/octet-stream")
            .build()
        s3.putObject(request, RequestBody.fromInputStream(file.inputStream, file.size))
    }
}
