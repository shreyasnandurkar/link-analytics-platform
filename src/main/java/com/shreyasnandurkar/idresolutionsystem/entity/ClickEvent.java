package com.shreyasnandurkar.idresolutionsystem.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "click_event", indexes = {
        @Index(name = "idx_click_sk_ca_city", columnList = "short_key, clicked_at, country, city, new_visitor")
})
@Getter
@NoArgsConstructor
public class ClickEvent {

    @Id
    @GeneratedValue
    private Long clickId;

    @Column(name = "short_key", nullable = false)
    private String shortKey;

    @Column(name = "clicked_at", nullable = false)
    private LocalDateTime clickedAt;

    @Column(name = "ip_address_hash")
    private String ipAddressHash;

    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String country;

    @Column(name = "new_visitor")
    private boolean newVisitor;

    @Column(name = "is_mobile")
    private boolean isMobile;

    public ClickEvent(String shortKey, String ipAddressHash, String city, String country, boolean newVisitor,
                      boolean isMobile) {
        this.shortKey = shortKey;
        this.clickedAt = LocalDateTime.now();
        this.ipAddressHash = ipAddressHash;
        this.city = city;
        this.country = country;
        this.newVisitor = newVisitor;
        this.isMobile = isMobile;
    }
}
