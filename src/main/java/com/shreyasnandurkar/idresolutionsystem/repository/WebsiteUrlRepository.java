package com.shreyasnandurkar.idresolutionsystem.repository;

import com.shreyasnandurkar.idresolutionsystem.entity.LinkItemResponse;
import com.shreyasnandurkar.idresolutionsystem.entity.WebsiteUrl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface WebsiteUrlRepository extends JpaRepository<WebsiteUrl, UUID> {

    WebsiteUrl findByShortKey(String shortKey);

    boolean existsByShortKeyAndUserId(String shortKey, String userId);

    @Query("SELECT w.shortKey FROM WebsiteUrl w WHERE w.userId = :userId")
    List<String> findAllShortKeysByUserId(@Param("userId") String userId);

    @Query("SELECT u.shortKey FROM WebsiteUrl u")
    List<String> findAllShortKeys();

    @Modifying
    @Query("DELETE FROM WebsiteUrl w WHERE w.shortKey = :shortKey AND w.userId = :userId")
    int deleteByShortKeyAndUserId(@Param("shortKey") String shortKey, @Param("userId") String userId);

    @Query("""
        SELECT new com.shreyasnandurkar.idresolutionsystem.entity.LinkItemResponse(
            w.shortKey, w.shortKey, w.originalUrl, w.createdAt
        )
        FROM WebsiteUrl w
        WHERE w.userId = :userId
    """)
    Page<LinkItemResponse> findUserLinks(@Param("userId") String userId, Pageable pageable);
}
