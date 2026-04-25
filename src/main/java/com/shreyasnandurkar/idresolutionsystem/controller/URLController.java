package com.shreyasnandurkar.idresolutionsystem.controller;

import com.shreyasnandurkar.idresolutionsystem.entity.*;
import com.shreyasnandurkar.idresolutionsystem.service.DashboardService;
import com.shreyasnandurkar.idresolutionsystem.service.OwnerService;
import com.shreyasnandurkar.idresolutionsystem.service.URLShortenerService;
import com.shreyasnandurkar.idresolutionsystem.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;

import java.net.URI;
import java.util.Optional;

@RestController
public class URLController {
    private final URLShortenerService urlShortenerService;
    private final DashboardService dashboardService;
    private final OwnerService ownerService;
    private final UserService userService;


    public URLController(URLShortenerService urlShortenerService,
                         DashboardService dashboardService, OwnerService ownerService, UserService userService) {
        this.urlShortenerService = urlShortenerService;
        this.dashboardService = dashboardService;
        this.ownerService = ownerService;
        this.userService = userService;
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("GoLinkGone OK");
    }

    @GetMapping("/info")
    public ResponseEntity<String> info() {
        return ResponseEntity.ok("This is GoLineGone :P");
    }

    @PostMapping("/create")
    public ResponseEntity<CreateResponse> createShortLink(@Valid @RequestBody CreateRequest request,
                                                          @AuthenticationPrincipal Jwt jwt) {

        String userId = (jwt != null) ? jwt.getSubject() : null;
        CreateResponse response = urlShortenerService.createShortLink(request.originalUrl(), userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{shortKey}")
    public ResponseEntity<Void> redirectUrl(@PathVariable String shortKey, HttpServletRequest request) {

        String ip = Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                .map(x -> x.split(",")[0].trim())
                .orElse(request.getRemoteAddr());

        String userAgent = request.getHeader("User-Agent");

        String originalUrl = urlShortenerService.redirectUrl(shortKey, ip, userAgent);
        return ResponseEntity.status(302).location(URI.create(originalUrl)).build();
    }

    @GetMapping("/my-links")
    public ResponseEntity<Page<LinkItemResponse>> getMyLinks(@AuthenticationPrincipal Jwt jwt,
                                                 @RequestParam(defaultValue = "0")int page,
                                                 @RequestParam(defaultValue = "30")int size){


        Page<LinkItemResponse> links = urlShortenerService.getUserLinks(jwt.getSubject(), page, size);
        return ResponseEntity.ok(links);
    }

    @GetMapping("/{shortKey}/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard(@PathVariable String shortKey, @RequestParam(defaultValue =
            "24h") String timeRange, @AuthenticationPrincipal Jwt jwt) {

        if (!ownerService.isOwner(shortKey, jwt.getSubject())) {
            throw new AccessDeniedException("Access Denied");
        }

        DashboardResponse response = dashboardService.getDashboard(shortKey, timeRange);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{shortKey}")
    public ResponseEntity<Void> deleteLink(@PathVariable String shortKey, @AuthenticationPrincipal Jwt jwt) {

        urlShortenerService.deleteLink(shortKey, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/account")
    public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal Jwt jwt) {
        userService.deleteAccount(jwt.getSubject());
        return ResponseEntity.noContent().build();
    }

}
