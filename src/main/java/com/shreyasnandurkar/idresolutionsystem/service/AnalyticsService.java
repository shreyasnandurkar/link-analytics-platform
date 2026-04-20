package com.shreyasnandurkar.idresolutionsystem.service;

import com.google.common.hash.Hashing;
import com.shreyasnandurkar.idresolutionsystem.entity.ClickEvent;
import com.shreyasnandurkar.idresolutionsystem.entity.GeoLocation;
import com.shreyasnandurkar.idresolutionsystem.entity.VisitorFingerprint;
import com.shreyasnandurkar.idresolutionsystem.repository.ClickEventRepository;
import com.shreyasnandurkar.idresolutionsystem.repository.VisitorFingerprintRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class AnalyticsService {
    private final ClickEventRepository clickEventRepository;
    private final VisitorFingerprintRepository fingerprintRepository;
    private final GeoIPService geoIPService;
    private AnalyticsService self;

    public AnalyticsService(ClickEventRepository clickEventRepository,
                            VisitorFingerprintRepository fingerprintRepository, GeoIPService geoIPService) {
        this.clickEventRepository = clickEventRepository;
        this.fingerprintRepository = fingerprintRepository;
        this.geoIPService = geoIPService;
    }

    @Async("analyticsExecutor")
    public void recordClick(String shortKey, String rawIp) {
        try {
            if (!StringUtils.hasText(rawIp)) {
                log.warn("Skipping click tracking for shortKey={} because IP address is blank", shortKey);
                return;
            }

            String ipHash = hashIpAddress(rawIp);
            GeoLocation location = geoIPService.lookup(rawIp);
            self.persistClick(shortKey, ipHash, location);
        } catch (Exception ex) {
            log.error("Failed to record click for shortKey={}", shortKey, ex);
        }
    }

    @Transactional
    public void persistClick(String shortKey, String ipHash, GeoLocation location) {
        boolean isNew = tryInsertFingerprint(shortKey, ipHash);
        clickEventRepository.save(new ClickEvent(
                shortKey,
                ipHash,
                location.city(),
                location.country(),
                isNew
        ));
    }

    private boolean tryInsertFingerprint(String shortKey, String ipHash) {
        try {
            fingerprintRepository.save(new VisitorFingerprint(shortKey, ipHash));
            return true;
        } catch (DataIntegrityViolationException e) {
            log.debug("Visitor fingerprint already exists for shortKey={}", shortKey);
            return false;
        }
    }

    private String hashIpAddress(String rawIp) {
        return Hashing.sha256().hashString(rawIp, StandardCharsets.UTF_8).toString();
    }

    @Autowired
    public void setSelf(@Lazy AnalyticsService self) {
        this.self = self;
    }
}
