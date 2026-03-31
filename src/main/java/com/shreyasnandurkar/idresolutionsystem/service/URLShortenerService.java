    package com.shreyasnandurkar.idresolutionsystem.service;

    import com.shreyasnandurkar.idresolutionsystem.config.AppProperties;
    import com.shreyasnandurkar.idresolutionsystem.config.KeyStore;
    import com.shreyasnandurkar.idresolutionsystem.entity.LinkType;
    import com.shreyasnandurkar.idresolutionsystem.entity.WebsiteUrl;
    import com.shreyasnandurkar.idresolutionsystem.repository.WebsiteUrlRepository;
    import org.springframework.dao.DuplicateKeyException;
    import org.springframework.stereotype.Service;

    @Service
    public class URLShortenerService {
        private final AppProperties appProperties;
        private final WebsiteUrlRepository repository;
        private final CounterService counterService;
        private final UrlLookupService urlLookupService;
        private final KeyStore keyStore;

        public URLShortenerService(AppProperties appProperties, WebsiteUrlRepository repository,
                                   CounterService counterService, UrlLookupService urlLookupService, KeyStore keyStore) {
            this.appProperties = appProperties;
            this.repository = repository;
            this.counterService = counterService;
            this.urlLookupService = urlLookupService;
            this.keyStore = keyStore;
        }

        public String createShortLink(String originalUrl, LinkType type){
            String shortKey;
            do {
                shortKey = ShortKeyGenerator.generateShortKey();
            } while (keyStore.contains(shortKey));

            try {
                repository.save(new WebsiteUrl(originalUrl, shortKey, type));
                keyStore.addKey(shortKey);
            } catch (DuplicateKeyException ignored) {}

            return appProperties.getBaseUrl() + "/" + shortKey;
        }

        public String redirectUrl(String shortKey){
            if(!keyStore.contains(shortKey)) throw new RuntimeException("Invalid URL");

            String originalUrl = urlLookupService.getOriginalUrl(shortKey);
            counterService.incrementRedirectCount(shortKey);
            return originalUrl;
        }
    }
