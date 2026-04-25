package com.shreyasnandurkar.idresolutionsystem.service;

import com.google.common.hash.Hashing;
import com.shreyasnandurkar.idresolutionsystem.entity.GeoLocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class AnalyticsService {
    private final GeoIPService geoIPService;
    private final ClickPersistService clickPersistService;

    public AnalyticsService(GeoIPService geoIPService, ClickPersistService clickPersistService) {

        this.geoIPService = geoIPService;
        this.clickPersistService = clickPersistService;
    }

    @Async("analyticsExecutor")
    public void recordClick(String shortKey, String rawIp, String userAgent) {
        try {
            if (!StringUtils.hasText(rawIp)) {
                log.warn("Skipping click tracking for shortKey={} because IP address is blank", shortKey);
                return;
            }

            String ipHash = hashIpAddress(rawIp);
            GeoLocation location = geoIPService.lookup(rawIp);
            boolean isMobile = isMobileDevice(userAgent);
            clickPersistService.persistClick(shortKey, ipHash, location, isMobile);
        } catch (Exception ex) {
            log.error("Failed to record click for shortKey={}", shortKey, ex);
        }
    }

    private String hashIpAddress(String rawIp) {
        return Hashing.sha256().hashString(rawIp, StandardCharsets.UTF_8).toString();
    }

    private boolean isMobileDevice(String userAgent) {
        if (!StringUtils.hasText(userAgent)) return false;
        String lowerAgent = userAgent.toLowerCase();
        return lowerAgent.contains("mobi") ||
                lowerAgent.contains("android") ||
                lowerAgent.contains("iphone");
    }
}
