package com.shreyasnandurkar.idresolutionsystem.service;

import com.shreyasnandurkar.idresolutionsystem.config.KeyStore;
import com.shreyasnandurkar.idresolutionsystem.repository.WebsiteUrlRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final SupabaseAdminClient supabaseAdminClient;
    private final WebsiteUrlRepository urlRepository;
    private final KeyStore keyStore;
    private final CacheManager cacheManager;


    public UserService(SupabaseAdminClient supabaseAdminClient, WebsiteUrlRepository urlRepository, KeyStore keyStore, CacheManager cacheManager) {
        this.supabaseAdminClient = supabaseAdminClient;
        this.urlRepository = urlRepository;
        this.keyStore = keyStore;
        this.cacheManager = cacheManager;
    }

    public void deleteAccount(String userId) {
        List<String> userKeys = urlRepository.findAllShortKeysByUserId(userId);

        supabaseAdminClient.deleteUser(userId);

        Cache urlCache = cacheManager.getCache("urlCache");
        Cache ownershipCache = cacheManager.getCache("ownershipCache");

        userKeys.forEach(key -> {
            keyStore.removeKey(key);
            if (urlCache != null) urlCache.evict(key);
            if (ownershipCache != null) ownershipCache.evict(key + "_" + userId);
        });
    }

}
