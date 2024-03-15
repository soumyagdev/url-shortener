package com.soumya.urlshortener.controller;

import com.soumya.urlshortener.model.Url;
import com.soumya.urlshortener.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

// @RestController — this class handles HTTP requests
// Combines @Controller + @ResponseBody
// Every method returns data directly (JSON) not a view
@CrossOrigin(origins = "*")
@RestController

// @RequestMapping — all endpoints in this class start with /api
@RequestMapping("/api")
public class UrlController {

    // Spring injects UrlService automatically
    @Autowired
    private UrlService urlService;

    // ── ENDPOINT 1: Shorten a URL ──
    // POST /api/shorten
    // User sends: { "url": "https://google.com", "expiryDays": 30 }
    // Returns:    { "shortCode": "x7k2p", "shortUrl": "http://localhost:8080/x7k2p" }
    @PostMapping("/shorten")
    public ResponseEntity<Map<String, Object>> shortenUrl(
            @RequestBody Map<String, Object> request) {

        // Get the original URL from request body
        String originalUrl = (String) request.get("url");
        
        // Get expiry days — default 0 means never expires
        int expiryDays = request.containsKey("expiryDays") ? 
            (Integer) request.get("expiryDays") : 0;

        // Basic validation — URL cannot be empty
        if (originalUrl == null || originalUrl.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "URL cannot be empty");
            return ResponseEntity.badRequest().body(error);
        }

        // Call service to shorten the URL
        Url url = urlService.shortenUrl(originalUrl, expiryDays);

        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("shortCode", url.getShortCode());
        response.put("shortUrl", "http://localhost:8080/" + url.getShortCode());
        response.put("originalUrl", url.getOriginalUrl());
        response.put("expiresAt", url.getExpiresAt());

        // 201 Created — standard response for successful resource creation
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── ENDPOINT 2: Redirect to original URL ──
    // GET /{shortCode}
    // User hits: http://localhost:8080/x7k2p
    // Redirects to original URL
    @GetMapping("/{shortCode}")
    public ResponseEntity<Map<String, Object>> redirect(
            @PathVariable String shortCode) {

        // Ask service for the original URL
        Optional<Url> urlOptional = urlService.getUrl(shortCode);

        if (urlOptional.isPresent()) {
            Url url = urlOptional.get();
            
            // Build redirect response
            Map<String, Object> response = new HashMap<>();
            response.put("originalUrl", url.getOriginalUrl());
            response.put("message", "Redirect to original URL");
            
            // 302 Found — standard HTTP redirect status
            return ResponseEntity
                .status(HttpStatus.FOUND)
                .header("Location", url.getOriginalUrl())
                .body(response);
        }

        // Short code not found or expired
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Short URL not found or expired");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // ── ENDPOINT 3: Get stats for a short URL ──
    // GET /api/stats/{shortCode}
    // Returns: click count, created date, expiry date
    @GetMapping("/stats/{shortCode}")
    public ResponseEntity<Map<String, Object>> getStats(
            @PathVariable String shortCode) {

        Optional<Url> urlOptional = urlService.getStats(shortCode);

        if (urlOptional.isPresent()) {
            Url url = urlOptional.get();
            
            Map<String, Object> response = new HashMap<>();
            response.put("shortCode", url.getShortCode());
            response.put("originalUrl", url.getOriginalUrl());
            response.put("clickCount", url.getClickCount());
            response.put("createdAt", url.getCreatedAt());
            response.put("expiresAt", url.getExpiresAt());
            
            return ResponseEntity.ok(response);
        }

        Map<String, Object> error = new HashMap<>();
        error.put("error", "Short URL not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}