package com.shreyasnandurkar.idresolutionsystem.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "click_event", indexes = {
        @Index(name = "idx_click_short_key_clicked_at", columnList = "short_key, clicked_at"),
        @Index(name = "idx_click_clicked_at", columnList = "clicked_at")
})
@Getter
@NoArgsConstructor
public class ClickEvent {

    @Id
    private UUID clickId;

    @Column(name = "short_key", nullable = false)
    private String shortKey;

    @Column(name="clicked_at", nullable = false)
    private LocalDateTime clickedAt;

    @Column(name = "ip_address_hash")
    private String ipAddressHash;

    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String country;

    @Column(name = "new_visitor")
    private boolean newVisitor;

    public ClickEvent(String shortKey, String ipAddressHash, String city, String country, boolean newVisitor) {
        this.clickId = UuidCreator.getTimeOrderedEpoch();
        this.shortKey = shortKey;
        this.clickedAt = LocalDateTime.now();
        this.ipAddressHash = ipAddressHash;
        this.city = city;
        this.country = country;
        this.newVisitor = newVisitor;
    }
}
