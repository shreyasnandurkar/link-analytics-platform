package com.shreyasnandurkar.idresolutionsystem.repository;

import com.shreyasnandurkar.idresolutionsystem.entity.WebsiteUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WebsiteUrlRepository extends JpaRepository<WebsiteUrl, UUID> {
    boolean existsByShortKey(String key);
    WebsiteUrl findByShortKey(String shortKey);
}
