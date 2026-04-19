package com.shreyasnandurkar.idresolutionsystem.repository;


import com.shreyasnandurkar.idresolutionsystem.entity.CityStats;
import com.shreyasnandurkar.idresolutionsystem.entity.ClickEvent;
import com.shreyasnandurkar.idresolutionsystem.entity.ClickStats;
import com.shreyasnandurkar.idresolutionsystem.entity.CountryStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ClickEventRepository extends JpaRepository<ClickEvent, UUID> {

    @Query(value = """
            SELECT DATE_TRUNC(CAST(:granularity AS text), c.clicked_at) AS bucket,
                COUNT(*) AS total,
                COUNT(*) FILTER (WHERE c.new_visitor = true) AS new_visitors
            FROM click_event c
            WHERE c.short_key = :shortKey
                AND c.clicked_at >= :from AND c.clicked_at < :to
            GROUP BY bucket
            ORDER BY bucket""", nativeQuery = true)
    List<ClickStats> getTotals(
            @Param("shortKey") String shortKey,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("granularity") String granularity
    );

    @Query(value = """
            SELECT c.country AS country,
                   COUNT(*) AS total,
                   COUNT(*) FILTER (WHERE c.new_visitor = true) AS new_visitors
            FROM click_event c
            WHERE c.short_key = :shortKey
              AND c.clicked_at >= :from
              AND c.clicked_at < :to
              AND c.country IS NOT NULL
            GROUP BY c.country
            ORDER BY total DESC
            """, nativeQuery = true)
    List<CountryStats> getTopCountries(
            @Param("shortKey") String shortKey,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query(value = """
            SELECT c.city AS city,
                   c.country AS country,
                   COUNT(*) AS total,
                   COUNT(*) FILTER (WHERE c.new_visitor = true) AS new_visitors
            FROM click_event c
            WHERE c.short_key = :shortKey
              AND c.clicked_at >= :from
              AND c.clicked_at < :to
              AND c.city IS NOT NULL
              AND c.country IS NOT NULL
            GROUP BY c.city, c.country
            ORDER BY total DESC
            """, nativeQuery = true)
    List<CityStats> getTopCities(
            @Param("shortKey") String shortKey,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}
