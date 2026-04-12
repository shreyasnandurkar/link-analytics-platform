package com.shreyasnandurkar.idresolutionsystem.entity;

public record GeoLocation(
        String continent,
        String country,
        String regionName,
        String city,
        Boolean mobile
)
{}
