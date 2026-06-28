package com.noizy.infrastructure.aws

import com.noizy.domain.exception.NotFoundException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.ResponseInputStream
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectResponse
import software.amazon.awssdk.services.s3.model.HeadObjectRequest
import software.amazon.awssdk.services.s3.model.NoSuchKeyException
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.util.UUID

data class S3ObjectStream(
    val stream: ResponseInputStream<GetObjectResponse>,
    val contentType: String,
    val totalLength: Long,
    val returnedLength: Long
)

data class S3BinaryObject(
    val stream: ResponseInputStream<GetObjectResponse>,
    val contentType: String,
    val contentLength: Long
)

@Service
class S3StorageService(
    private val s3: S3Client,
    @Value("\${noizy.aws.s3.tracks-bucket}") private val tracksBucket: String,
    @Value("\${noizy.aws.s3.images-bucket}") private val imagesBucket: String
) {
    fun uploadAudio(file: MultipartFile): String {
        val extension = file.originalFilename?.substringAfterLast('.', "mp3") ?: "mp3"
        val key = "tracks/${UUID.randomUUID()}.$extension"
        put(tracksBucket, key, file)
        return key
    }

    fun uploadCover(file: MultipartFile): String {
        val extension = file.originalFilename?.substringAfterLast('.', "jpg") ?: "jpg"
        val key = "covers/${UUID.randomUUID()}.$extension"
        put(imagesBucket, key, file)
        return key
    }

    fun getAudio(key: String, rangeHeader: String?): S3ObjectStream {
        try {
            val head = s3.headObject(HeadObjectRequest.builder().bucket(tracksBucket).key(key).build())
            val builder = GetObjectRequest.builder().bucket(tracksBucket).key(key)
            if (!rangeHeader.isNullOrBlank()) {
                builder.range(rangeHeader)
            }
            val objectStream = s3.getObject(builder.build())
            return S3ObjectStream(
                stream = objectStream,
                contentType = objectStream.response().contentType() ?: "audio/mpeg",
                totalLength = head.contentLength(),
                returnedLength = objectStream.response().contentLength()
            )
        } catch (_: NoSuchKeyException) {
            throw NotFoundException("Audio object")
        }
    }

    fun getImage(key: String): S3BinaryObject {
        try {
            val objectStream = s3.getObject(GetObjectRequest.builder().bucket(imagesBucket).key(key).build())
            return S3BinaryObject(
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
