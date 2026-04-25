package com.shreyasnandurkar.idresolutionsystem.entity;

public record CreateResponse(
        String shortUrl,
        byte[] qrCode
) {
}
