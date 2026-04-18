package com.shreyasnandurkar.idresolutionsystem.controller;

import com.google.zxing.WriterException;
import com.shreyasnandurkar.idresolutionsystem.entity.CreateRequest;
import com.shreyasnandurkar.idresolutionsystem.entity.DashboardResponse;
import com.shreyasnandurkar.idresolutionsystem.entity.LinkType;
import com.shreyasnandurkar.idresolutionsystem.service.BarcodeService;
import com.shreyasnandurkar.idresolutionsystem.service.DashboardService;
import com.shreyasnandurkar.idresolutionsystem.service.QRCodeService;
import com.shreyasnandurkar.idresolutionsystem.service.URLShortenerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

@RestController
//@RequestMapping("/v1")
public class URLController {
    private final URLShortenerService urlShortenerService;
    private final QRCodeService qrCodeService;
    private final BarcodeService barcodeService;
    private final DashboardService dashboardService;

    public URLController(URLShortenerService urlShortenerService, QRCodeService qrCodeService,
                         BarcodeService barcodeService, DashboardService dashboardService) {
        this.urlShortenerService = urlShortenerService;
        this.qrCodeService = qrCodeService;
        this.barcodeService = barcodeService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck(){
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/info")
    public ResponseEntity<String> info(){
        return ResponseEntity.ok("This is a URL Shortener API");
    }

    @PostMapping("/shorten")
    public ResponseEntity<String> createShortLink(@RequestBody CreateRequest request){
        String shortUrl = urlShortenerService.createShortLink(request.originalUrl(), LinkType.SHORT_LINK);
        return ResponseEntity.ok(shortUrl);
    }

    @GetMapping("/{shortKey}")
    public ResponseEntity<Void> redirectUrl(@PathVariable String shortKey, HttpServletRequest request){
        String ip = Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                .map(x -> x.split(",")[0].trim())
                .orElse(request.getRemoteAddr());
        String originalUrl = urlShortenerService.redirectUrl(shortKey, ip);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(originalUrl)).build();
    }

    @PostMapping("/qr")
    public ResponseEntity<byte[]> createQRCode(@RequestBody CreateRequest request) throws IOException, WriterException {
        String shortUrl = urlShortenerService.createShortLink(request.originalUrl(), LinkType.QR_CODE);
        byte[] qrcode = qrCodeService.generateQrImage(shortUrl, 200, 200);
        return ResponseEntity.ok().header("Content-Type", "image/png").body(qrcode);
    }

    @PostMapping("/barcode")
    public ResponseEntity<byte[]> createBarCode(@RequestBody CreateRequest request) throws IOException,
            WriterException {
        String shortUrl = urlShortenerService.createShortLink(request.originalUrl(), LinkType.BARCODE);
        byte[]barcode = barcodeService.generateBarcode(shortUrl, 200, 200);
        return ResponseEntity.ok().header("Content-Type", "image/png").body(barcode);
    }

    public ResponseEntity<DashboardResponse> getDashboard(@PathVariable String shortKey, @RequestParam(defaultValue = "24h") String timeRange){
        DashboardResponse response = dashboardService.getAnalytics(shortKey, timeRange);
        return ResponseEntity.ok(response);
    }
}
