package com.shreyasnandurkar.idresolutionsystem.service;

import com.shreyasnandurkar.idresolutionsystem.repository.WebsiteUrlRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class OwnerService {

    private final WebsiteUrlRepository urlRepository;

    public OwnerService(WebsiteUrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Cacheable(value = "ownershipCache", key = "#shortKey + '_' + #userId")
    public boolean isOwner(String shortKey, String userId) {
        return urlRepository.existsByShortKeyAndUserId(shortKey, userId);
    }
}
