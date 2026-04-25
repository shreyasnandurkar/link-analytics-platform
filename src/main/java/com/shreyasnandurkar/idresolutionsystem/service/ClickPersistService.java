package com.shreyasnandurkar.idresolutionsystem.service;

import com.shreyasnandurkar.idresolutionsystem.entity.ClickEvent;
import com.shreyasnandurkar.idresolutionsystem.entity.GeoLocation;
import com.shreyasnandurkar.idresolutionsystem.entity.VisitorFingerprint;
import com.shreyasnandurkar.idresolutionsystem.repository.ClickEventRepository;
import com.shreyasnandurkar.idresolutionsystem.repository.VisitorFingerprintRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ClickPersistService {

    private final ClickEventRepository clickEventRepository;
    private final VisitorFingerprintRepository fingerprintRepository;

    public ClickPersistService(ClickEventRepository clickEventRepository,
                         VisitorFingerprintRepository fingerprintRepository) {
        this.clickEventRepository = clickEventRepository;
        this.fingerprintRepository = fingerprintRepository;
    }

    @Transactional
    public void persistClick(String shortKey, String ipHash, GeoLocation location, boolean isMobile) {
        boolean isNew = tryInsertFingerprint(shortKey, ipHash);
        clickEventRepository.save(new ClickEvent(
                shortKey,
                ipHash,
                location.city(),
                location.country(),
                isNew,
                isMobile
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

}
