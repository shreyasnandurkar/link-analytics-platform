    package com.shreyasnandurkar.idresolutionsystem.service;

    import com.shreyasnandurkar.idresolutionsystem.config.AppProperties;
    import com.shreyasnandurkar.idresolutionsystem.entity.LinkType;
    import com.shreyasnandurkar.idresolutionsystem.entity.WebsiteUrl;
    import com.shreyasnandurkar.idresolutionsystem.repository.WebsiteUrlRepository;
    import jakarta.transaction.Transactional;
    import org.springframework.http.HttpStatus;
    import org.springframework.stereotype.Service;
    import org.springframework.web.server.ResponseStatusException;

    import java.util.Optional;

    @Service
    public class URLShortenerService {
        private final AppProperties appProperties;
        private final WebsiteUrlRepository repository;

        public URLShortenerService(AppProperties appProperties, WebsiteUrlRepository repository) {
            this.appProperties = appProperties;
            this.repository = repository;
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

        @Transactional
        public String redirectUrl(String shortKey){
            System.out.println("Looking for key: " + shortKey);
            Optional<WebsiteUrl> entity =
                    Optional.ofNullable(repository.findByShortKey(shortKey));

            if(entity.isEmpty())
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid URL");

            entity.get().increaseRedirectCount();
            repository.save(entity.get());
            return entity.get().getOriginalUrl();
        }
    }
