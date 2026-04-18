package com.shreyasnandurkar.idresolutionsystem.service;

import com.google.common.hash.Hashing;
import com.shreyasnandurkar.idresolutionsystem.entity.ClickEvent;
import com.shreyasnandurkar.idresolutionsystem.entity.GeoLocation;
import com.shreyasnandurkar.idresolutionsystem.entity.VisitorFingerprint;
import com.shreyasnandurkar.idresolutionsystem.repository.ClickEventRepository;
import com.shreyasnandurkar.idresolutionsystem.repository.VisitorFingerprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@Service
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
        String ipHash = hashIpAddress(rawIp);
        GeoLocation location = geoIPService.lookup(rawIp);
        self.persistClick(shortKey, ipHash, location);
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
        try{
            fingerprintRepository.save(new VisitorFingerprint(shortKey, ipHash));
            return true;
        }
        catch (DataIntegrityViolationException e){
            return false;
        }
    }

    private String hashIpAddress(String rawIp){
        return Hashing.sha256().hashString(rawIp, StandardCharsets.UTF_8).toString();
    }

    @Autowired
    public void setSelf(@Lazy AnalyticsService self) {
        this.self = self;
    }
}
