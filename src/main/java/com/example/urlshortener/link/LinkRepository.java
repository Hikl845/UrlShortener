package com.example.urlshortener.link;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LinkRepository extends JpaRepository<ShortLink, Long> {

    Optional<ShortLink> findByShortCode(String shortCode);

    List<ShortLink> findAllByUserId(Long userId);

    boolean existsByShortCode(String shortCode);

    @Modifying
    @Query("update ShortLink l set l.clickCount = l.clickCount + 1 where l.shortCode = :code")
    void incrementClick(@Param("code") String code);
}
