package com.noizy.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "tracks")
class TrackEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(nullable = false, length = 220)
    var title: String = "",

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artist_id", nullable = false)
    var artist: ArtistEntity = ArtistEntity(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    var album: AlbumEntity? = null,

    @Column(length = 120)
    var genre: String? = null,

    @Column(name = "duration_seconds", nullable = false)
    var durationSeconds: Int = 0,

    @Column(name = "audio_s3_key", nullable = false, columnDefinition = "TEXT")
    var audioS3Key: String = "",

    @Column(name = "cover_s3_key", columnDefinition = "TEXT")
    var coverS3Key: String? = null,

    @Column(name = "play_count", nullable = false)
    var playCount: Long = 0,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
) {
    @PrePersist
    fun prePersist() {
        val now = Instant.now()
        createdAt = now
        updatedAt = now
    }

    @PreUpdate
    fun preUpdate() {
        updatedAt = Instant.now()
    }
}
