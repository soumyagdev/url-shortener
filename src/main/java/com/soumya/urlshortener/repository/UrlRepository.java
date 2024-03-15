package com.soumya.urlshortener.repository;

import com.soumya.urlshortener.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

// @Repository — tells Spring Boot this is a database layer class
// Spring will automatically create an instance of this
@Repository

// JpaRepository<Url, Long> means:
// Url = which table/model we are working with
// Long = the data type of the primary key (id)
// By extending JpaRepository we get these methods for FREE:
// save(), findById(), findAll(), delete(), count() etc.
public interface UrlRepository extends JpaRepository<Url, Long> {

    // Custom method — find a URL by its short code
    // Spring reads the method name and writes the SQL automatically
    // This becomes: SELECT * FROM urls WHERE short_code = ?
    Optional<Url> findByShortCode(String shortCode);

    // Check if a short code already exists
    // This becomes: SELECT COUNT(*) FROM urls WHERE short_code = ?
    boolean existsByShortCode(String shortCode);

}