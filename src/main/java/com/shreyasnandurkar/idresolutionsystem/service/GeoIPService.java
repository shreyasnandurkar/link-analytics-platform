package com.shreyasnandurkar.idresolutionsystem.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.shreyasnandurkar.idresolutionsystem.entity.GeoLocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
public class GeoIPService {

    private final GeoLocation UNKNOWN  = new GeoLocation("UNKNOWN", "UNKNOWN", "UNKNOWN", "UNKNOWN", false);

    private final RestClient restClient = RestClient.builder().requestFactory(buildRequestFactory()).build();

    private static JdkClientHttpRequestFactory buildRequestFactory() {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();

        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(2));
        return factory;
    }

    private final Cache<String, GeoLocation> successCache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(Duration.ofHours(24))
            .build();
    private final Cache<String, GeoLocation> failureCache = Caffeine.newBuilder()
            .maximumSize(5_000)
            .expireAfterWrite(Duration.ofMinutes(5))
            .build();

    public GeoLocation lookup(String ip) {
        GeoLocation cached = successCache.getIfPresent(ip);
        if (cached != null)return cached;

        GeoLocation failed = failureCache.getIfPresent(ip);
        if (failed != null)return failed;

        try {
            String url = "http://ip-api.com/json/" + ip + "?fields=81945";

            @SuppressWarnings("unchecked")
            Map<String, Object> resp = restClient.get().uri(url).retrieve().body(Map.class);

            if (resp == null || "fail".equals(resp.get("status"))) {
                return cacheAndReturn(failureCache, ip, UNKNOWN);
            }

            GeoLocation location = new GeoLocation(
                    asString(resp.get("continent")),
                    asString(resp.get("country")),
                    asString(resp.get("regionName")),
                    asString(resp.get("city")),
                    Boolean.TRUE.equals(resp.get("mobile"))

            );

            return cacheAndReturn(successCache, ip, location);

        } catch (Exception e) {
            log.warn("GeoIP lookup failed for IP hash [{}]: {}", ip.hashCode(), e.getMessage());
            return cacheAndReturn(failureCache, ip, UNKNOWN);
        }
    }

    private GeoLocation cacheAndReturn(Cache<String, GeoLocation> cache, String ip, GeoLocation location) {
        cache.put(ip, location);
        return location;
    }

    private String asString(Object value) {
        return value instanceof String s ? s : null;
    }
}