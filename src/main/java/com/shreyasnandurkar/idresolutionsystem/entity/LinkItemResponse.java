package com.shreyasnandurkar.idresolutionsystem.entity;

import java.time.LocalDateTime;

public record LinkItemResponse (
    String shortKey,
    String shortUrl,
    String originalUrl,
    LocalDateTime createdAt
) {}
