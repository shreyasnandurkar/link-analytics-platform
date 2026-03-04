    package com.shreyasnandurkar.idresolutionsystem.service;

    import com.shreyasnandurkar.idresolutionsystem.config.AppProperties;
    import com.shreyasnandurkar.idresolutionsystem.entity.LinkType;
    import com.shreyasnandurkar.idresolutionsystem.entity.WebsiteUrl;
    import com.shreyasnandurkar.idresolutionsystem.repository.WebsiteUrlRepository;
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
            String key;
            do {
                key = ShortKeyGenerator.generateShortKey();
            } while (repository.existsByShortKey(key));

            WebsiteUrl entity = new WebsiteUrl(originalUrl, key, type);
            repository.save(entity);
            return appProperties.getBaseUrl() + "/" + key;
        }

        public String redirectUrl(String shortKey){
            String originalUrl = urlLookupService.getOriginalUrl(shortKey);
            counterService.incrementRedirectCount(shortKey);
            return originalUrl;
        }
    }
