    package com.shreyasnandurkar.idresolutionsystem.service;

    import com.shreyasnandurkar.idresolutionsystem.config.AppProperties;
    import com.shreyasnandurkar.idresolutionsystem.entity.LinkType;
    import com.shreyasnandurkar.idresolutionsystem.entity.WebsiteUrl;
    import com.shreyasnandurkar.idresolutionsystem.repository.WebsiteUrlRepository;
    import org.springframework.dao.DuplicateKeyException;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;


    @Service
    public class URLShortenerService {
        private final AppProperties appProperties;
        private final WebsiteUrlRepository repository;
        private final CounterService counterService;
        private final UrlLookupService urlLookupService;

        public URLShortenerService(AppProperties appProperties, WebsiteUrlRepository repository,
                                   CounterService counterService, UrlLookupService urlLookupService) {
            this.appProperties = appProperties;
            this.repository = repository;
            this.counterService = counterService;
            this.urlLookupService = urlLookupService;
        }

        @Transactional
        public String createShortLink(String originalUrl, LinkType type){
            while (true) {
                String key = ShortKeyGenerator.generateShortKey();
                try {
                    repository.save(new WebsiteUrl(originalUrl, key, type));
                    return appProperties.getBaseUrl() + "/" + key;
                } catch (DuplicateKeyException ignored) {}
            }
        }

        public String redirectUrl(String shortKey){
            String originalUrl = urlLookupService.getOriginalUrl(shortKey);
            counterService.incrementRedirectCount(shortKey);
            return originalUrl;
        }
    }
