package com.shreyasnandurkar.idresolutionsystem.service;

import com.shreyasnandurkar.idresolutionsystem.repository.WebsiteUrlRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class CounterService {

    private static final Logger log = LoggerFactory.getLogger(CounterService.class);
    private final WebsiteUrlRepository websiteUrlRepository;

    public CounterService(WebsiteUrlRepository websiteUrlRepository) {
        this.websiteUrlRepository = websiteUrlRepository;
    }

    @Async
    @Transactional
    public void incrementRedirectCount(String shortKey){
        try{
            websiteUrlRepository.incrementRedirectCount(shortKey);
        }
        catch (Exception e){
            log.error("Error incrementing redirect count for shortKey: {}", shortKey, e);
        }
    }
}
