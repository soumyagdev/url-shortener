package com.soumya.urlshortener.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "urls")
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String shortCode;

    @Column(nullable = false, length = 2048)
    private String originalUrl;

    @Column(nullable = false)
    private Long clickCount = 0L;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters
    public Long getId() { return id; }
    public String getShortCode() { return shortCode; }
    public String getOriginalUrl() { return originalUrl; }
    public Long getClickCount() { return clickCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setShortCode(String shortCode) { this.shortCode = shortCode; }
    public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }
    public void setClickCount(Long clickCount) { this.clickCount = clickCount; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}