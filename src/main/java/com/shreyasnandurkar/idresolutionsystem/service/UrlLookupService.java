package com.shreyasnandurkar.idresolutionsystem.service;

import com.shreyasnandurkar.idresolutionsystem.entity.WebsiteUrl;
import com.shreyasnandurkar.idresolutionsystem.repository.WebsiteUrlRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UrlLookupService {

    private final WebsiteUrlRepository repository;
    private final MeterRegistry meterRegistry;

    public UrlLookupService(WebsiteUrlRepository websiteUrlRepository, MeterRegistry meterRegistry) {
        this.repository = websiteUrlRepository;
        this.meterRegistry = meterRegistry;
    }

    @Cacheable(value = "urlCache", key = "#shortKey")
    public String getOriginalUrl(String shortKey){
        Timer.Sample sample = Timer.start(meterRegistry);

        WebsiteUrl entity = repository.findByShortKey(shortKey);

        sample.stop(
                Timer.builder("url.cache.miss.db.lookup")
                        .description("Time taken to fetch URL from DB on cache miss")
                        .register(meterRegistry)
        );
        if(entity==null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid URL");
        return entity.getOriginalUrl();
    }
}
