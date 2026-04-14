package com.shreyasnandurkar.idresolutionsystem.repository;

import com.shreyasnandurkar.idresolutionsystem.entity.WebsiteUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface WebsiteUrlRepository extends JpaRepository<WebsiteUrl, UUID> {

    WebsiteUrl findByShortKey(String shortKey);

    @Query("select w.shortKey from WebsiteUrl w")
    List<String> findAllshortKey();
}
