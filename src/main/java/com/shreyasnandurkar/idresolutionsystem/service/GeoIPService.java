package com.shreyasnandurkar.idresolutionsystem.service;

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.model.CityResponse;
import org.springframework.core.io.Resource;
import com.shreyasnandurkar.idresolutionsystem.entity.GeoLocation;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.InetAddress;
import java.util.regex.Pattern;

@Slf4j
@Service
public class GeoIPService {

    private static final GeoLocation UNKNOWN = new GeoLocation("UNKNOWN", "UNKNOWN", "UNKNOWN", "UNKNOWN");

    private static final Pattern VALID_IP = Pattern.compile(
            "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$"
    );

    @Value("${maxmind.db-path}")
    private Resource geoIPResource;

    private DatabaseReader reader;

    @PostConstruct
    public void init() {
        try {
            reader = new DatabaseReader.Builder(geoIPResource.getInputStream())
                    .withCache(new CHMCache()) //thread safe in-memory cache
                    .build();
            log.info("MaxMind GeoLite2 database loaded successfully");
        } catch (Exception e) {
            log.error("Failed to load MaxMind database — geo lookups will return UNKNOWN", e);
        }
    }

    public GeoLocation lookup(String ip) {
        if (ip == null || !VALID_IP.matcher(ip).matches()) {
            return UNKNOWN;
        }
        if (reader == null) {
            return UNKNOWN;
        }
        try {
            InetAddress address = InetAddress.getByName(ip);
            CityResponse response = reader.city(address);

            String continent = response.continent().name();
            String country   = response.country().name();
            String region    = response.mostSpecificSubdivision().name();
            String city      = response.city().name();

            return new GeoLocation(
                    continent != null ? continent : "UNKNOWN",
                    country   != null ? country   : "UNKNOWN",
                    region    != null ? region    : "UNKNOWN",
                    city      != null ? city      : "UNKNOWN"
            );
        } catch (AddressNotFoundException e) {
            log.debug("No geo data for IP hash [{}]", ip.hashCode());
            return UNKNOWN;
        } catch (Exception e) {
            log.warn("GeoIP lookup failed for IP hash [{}]: {}", ip.hashCode(), e.getMessage());
            return UNKNOWN;
        }
    }

}
