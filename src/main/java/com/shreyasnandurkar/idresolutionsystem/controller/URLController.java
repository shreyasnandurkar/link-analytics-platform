package com.shreyasnandurkar.idresolutionsystem.controller;

import com.google.zxing.WriterException;
import com.shreyasnandurkar.idresolutionsystem.entity.CreateRequest;
import com.shreyasnandurkar.idresolutionsystem.entity.LinkType;
import com.shreyasnandurkar.idresolutionsystem.service.BarcodeService;
import com.shreyasnandurkar.idresolutionsystem.service.QRCodeService;
import com.shreyasnandurkar.idresolutionsystem.service.URLShortenerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;

@RestController
//@RequestMapping("/v1")
public class URLController {
    private final URLShortenerService urlShortenerService;
    private final QRCodeService qrCodeService;
    private final BarcodeService barcodeService;

    public URLController(URLShortenerService urlShortenerService, QRCodeService qrCodeService, BarcodeService barcodeService) {
        this.urlShortenerService = urlShortenerService;
        this.qrCodeService = qrCodeService;
        this.barcodeService = barcodeService;
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
    public ResponseEntity<Void> redirectUrl(@PathVariable String shortKey){
        String originalUrl = urlShortenerService.redirectUrl(shortKey);
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
}
