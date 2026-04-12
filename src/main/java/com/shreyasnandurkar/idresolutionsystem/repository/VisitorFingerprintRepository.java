package com.shreyasnandurkar.idresolutionsystem.repository;

import com.shreyasnandurkar.idresolutionsystem.entity.VisitorFingerprint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitorFingerprintRepository extends JpaRepository<VisitorFingerprint, Long> {
    boolean existsByShortKeyAndIpAddressHash(String shortKey, String ipAddressHash);
}
