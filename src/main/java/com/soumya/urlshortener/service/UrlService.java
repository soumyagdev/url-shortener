package com.soumya.urlshortener.service;

import com.soumya.urlshortener.model.Url;
import com.soumya.urlshortener.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

// @Service — tells Spring Boot this is the business logic layer
// Spring automatically creates one instance and shares it everywhere
@Service
public class UrlService {

    // @Autowired — Spring automatically injects the UrlRepository
    // We don't create it manually with "new UrlRepository()"
    // Spring handles that for us — this is Dependency Injection
    @Autowired
    private UrlRepository urlRepository;

    // Characters we use to generate short codes
    // Base62 = a-z + A-Z + 0-9 = 62 characters
    private static final String CHARACTERS = 
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    
    // Length of our short code — 6 chars = 56 billion combinations
    private static final int SHORT_CODE_LENGTH = 6;

    // ── METHOD 1: Shorten a URL ──
    public Url shortenUrl(String originalUrl, int expiryDays) {
        
        // Step 1 — Generate a unique short code
        String shortCode = generateUniqueShortCode();
        
        // Step 2 — Create a new Url object
        Url url = new Url();
        url.setShortCode(shortCode);
        url.setOriginalUrl(originalUrl);
        url.setClickCount(0L);
        
        // Step 3 — Set expiry if provided
        // If expiryDays is 0, URL never expires
        if (expiryDays > 0) {
            url.setExpiresAt(LocalDateTime.now().plusDays(expiryDays));
        }
        
        // Step 4 — Save to database and return
        return urlRepository.save(url);
    }

    // ── METHOD 2: Get original URL by short code ──
    public Optional<Url> getUrl(String shortCode) {
        
        // Find the URL in database
        Optional<Url> urlOptional = urlRepository.findByShortCode(shortCode);
        
        // If found — increment click count and return
        if (urlOptional.isPresent()) {
            Url url = urlOptional.get();
            
            // Check if URL has expired
            if (url.getExpiresAt() != null && 
                url.getExpiresAt().isBefore(LocalDateTime.now())) {
                return Optional.empty(); // expired — return nothing
            }
            
            // Increment click count
            url.setClickCount(url.getClickCount() + 1);
            urlRepository.save(url);
            
            return Optional.of(url);
        }
        
        // Not found — return empty Optional
        return Optional.empty();
    }

    // ── METHOD 3: Get stats for a short code ──
    public Optional<Url> getStats(String shortCode) {
        return urlRepository.findByShortCode(shortCode);
    }

    // ── PRIVATE: Generate a unique short code ──
    private String generateUniqueShortCode() {
        Random random = new Random();
        String shortCode;
        
        // Keep generating until we get one that doesn't exist
        // Collision chance is extremely low with 56 billion combinations
        do {
            shortCode = generateShortCode(random);
        } while (urlRepository.existsByShortCode(shortCode));
        
        return shortCode;
    }

    // ── PRIVATE: Generate a random 6 character code ──
    private String generateShortCode(Random random) {
        StringBuilder sb = new StringBuilder(SHORT_CODE_LENGTH);
        
        // Pick 6 random characters from our Base62 set
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        
        return sb.toString();
    }
}