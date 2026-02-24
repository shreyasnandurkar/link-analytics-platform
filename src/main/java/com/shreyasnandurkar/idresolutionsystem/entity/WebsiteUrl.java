package com.shreyasnandurkar.idresolutionsystem.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "website_url",
        indexes = @Index(name = "idx_short_key", columnList = "shortKey")
)
@Getter
@NoArgsConstructor
public class WebsiteUrl {

    @Id
    private UUID linkId;

    @Column(nullable = false)
    private String originalUrl;

    @Column(nullable = false, unique = true, updatable = false)
    private String shortKey;

    @Enumerated(EnumType.STRING)
    private LinkType type;

    @Column(nullable = false)
    private Long redirectCount = 0L;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public WebsiteUrl(String originalUrl, String shortKey,LinkType type) {
        this.linkId = UuidCreator.getTimeOrderedEpoch();
        this.originalUrl = originalUrl;
        this.shortKey = shortKey;
        this.type = type;
    }

    @PrePersist
    public void prePersist(){
        this.createdAt = LocalDateTime.now();
    }

    public void increaseRedirectCount(){
        this.redirectCount++;
    }
}
