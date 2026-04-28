package com.example.urlshortener.link;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LinkRepository extends JpaRepository<ShortLink, Long> {

    Optional<ShortLink> findByShortCode(String shortCode);

    List<ShortLink> findAllByUserId(Long userId);

    List<ShortLink> findAllByUserIdAndExpiresAtAfter(Long userId, LocalDateTime now);

    boolean existsByShortCode(String shortCode);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query(
            "update ShortLink l set l.clickCount = l.clickCount + 1 where l.shortCode = :code"
    )
    void incrementClick(@org.springframework.data.repository.query.Param("code") String code);
}