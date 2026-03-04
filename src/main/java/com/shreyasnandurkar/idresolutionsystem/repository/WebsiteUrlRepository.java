package com.shreyasnandurkar.idresolutionsystem.repository;

import com.shreyasnandurkar.idresolutionsystem.entity.WebsiteUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface WebsiteUrlRepository extends JpaRepository<WebsiteUrl, UUID> {
    boolean existsByShortKey(String key);
    WebsiteUrl findByShortKey(String shortKey);

    @Modifying
    @Query("update WebsiteUrl w set w.redirectCount = w.redirectCount+1 where w.shortKey = :shortKey")
    void incrementRedirectCount(String shortKey);
}
