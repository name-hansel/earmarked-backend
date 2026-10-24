package com.earmarked.earmark.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
        name = "earmark",
        uniqueConstraints = @UniqueConstraint(
                name = Earmark.UQ_USER_ALBUM,
                columnNames = {"user_id", "spotify_album_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class Earmark {
    public static final String UQ_USER_ALBUM = "uq_earmark_user_album";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, updatable = false)
    private UUID userId;

    @Column(nullable = false, updatable = false, length = 32)
    private String spotifyAlbumId;

    @Column(length = 256)
    private String note;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "listened_at")
    private LocalDate listenedAt;

    @Column(nullable = false, length = 512)
    private String albumName;

    @Column(nullable = false)
    private LocalDate albumReleaseDate;

    @Column(nullable = false, length = 1024)
    private String albumArtists;
}
