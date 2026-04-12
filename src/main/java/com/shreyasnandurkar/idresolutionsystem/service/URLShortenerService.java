    package com.shreyasnandurkar.idresolutionsystem.service;

    import com.shreyasnandurkar.idresolutionsystem.config.AppProperties;
    import com.shreyasnandurkar.idresolutionsystem.config.KeyStore;
    import com.shreyasnandurkar.idresolutionsystem.entity.LinkType;
    import com.shreyasnandurkar.idresolutionsystem.entity.WebsiteUrl;
    import com.shreyasnandurkar.idresolutionsystem.exception.ShortKeyNotFoundException;
    import com.shreyasnandurkar.idresolutionsystem.repository.WebsiteUrlRepository;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.dao.DuplicateKeyException;
    import org.springframework.stereotype.Service;

    @Service
    @Slf4j
    public class URLShortenerService {
        private final AppProperties appProperties;
        private final WebsiteUrlRepository repository;
        private final AnalyticsService analyticsService;
        private final UrlLookupService urlLookupService;
        private final KeyStore keyStore;

        public URLShortenerService(AppProperties appProperties, WebsiteUrlRepository repository,
                                   AnalyticsService analyticsService, UrlLookupService urlLookupService, KeyStore keyStore) {
            this.appProperties = appProperties;
            this.repository = repository;
            this.analyticsService = analyticsService;
            this.urlLookupService = urlLookupService;
            this.keyStore = keyStore;
        }

        public String createShortLink(String originalUrl, LinkType type) {
            String shortKey = null;
            boolean saved = false;

            while (!saved) {
                String candidate = ShortKeyGenerator.generateShortKey();

                if (keyStore.contains(candidate)) continue;

                try {
                    repository.save(new WebsiteUrl(originalUrl, candidate, type));
                    keyStore.addKey(candidate);
                    shortKey = candidate;
                    saved = true;
                } catch (DuplicateKeyException e) {
                    log.debug("Short-key collision on '{}', retrying.", candidate);
                }
            }

            return appProperties.getBaseUrl() + "/" + shortKey;
        }

        public String redirectUrl(String shortKey, String ip) {

            if (!keyStore.contains(shortKey)) {
                throw new ShortKeyNotFoundException("No link found for key: " + shortKey);
            }

            String originalUrl = urlLookupService.getOriginalUrl(shortKey);
            analyticsService.recordClick(shortKey, ip);
            return originalUrl;
        }

    }
