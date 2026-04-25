package com.example.urlShortener.link;

import com.example.urlShortener.link.dto.LinkResponse;
import com.example.urlShortener.link.dto.LinkStatsResponse;
import com.example.urlShortener.user.User;
import com.example.urlShortener.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class LinkServiceImpl implements LinkService {

    private final LinkRepository repo;
    private final UserRepository userRepository;

    public LinkService(LinkRepository repo, UserRepository userRepository) {
        this.repo = repo;
        this.userRepository = userRepository;
    }

    // =========================
    // CREATE LINK
    // =========================
    public ShortLink create(String url, String username) {

        validateUrl(url); // 🔥 ДОДАНО

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        ShortLink link = new ShortLink();
        link.setOriginalUrl(url);
        link.setShortCode(generateUniqueCode());
        link.setUser(user);
        link.setCreatedAt(LocalDateTime.now());

        link.setExpiresAt(LocalDateTime.now().plusDays(7)); // 🔥 ДОДАНО
        link.setClickCount(0);

        return repo.save(link);
    }

    // =========================
    // OPEN LINK (REDIRECT)
    // =========================
    @Transactional
    public ShortLink openByCode(String code) {

        ShortLink link = repo.findByShortCode(code)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Link not found"));

        if (link.getExpiresAt() != null &&
                link.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.GONE, "Link expired");
        }

        repo.incrementClick(code);

        return link;
    }

    // =========================
    // GET USER LINKS
    // =========================
    public List<LinkResponse> getUserLinks(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return repo.findAllByUserId(user.getId())
                .stream()
                .map(link -> new LinkResponse(
                        link.getShortCode(),
                        link.getOriginalUrl(),
                        link.getClickCount()
                ))
                .toList();
    }

    // =========================
    // DELETE LINK
    // =========================
    public void delete(Long id, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        ShortLink link = repo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Link not found"));

        if (!link.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your link");
        }

        repo.delete(link);
    }

    // =========================
    // STATS
    // =========================
    public LinkStatsResponse getStats(String code) {

        ShortLink link = repo.findByShortCode(code)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Link not found"));

        boolean active = link.getExpiresAt() == null ||
                link.getExpiresAt().isAfter(LocalDateTime.now());

        return new LinkStatsResponse(
                link.getShortCode(),
                link.getOriginalUrl(),
                link.getClickCount(),
                active
        );
    }

    // =========================
    // VALIDATE URL 🔥 НОВЕ
    // =========================
    private void validateUrl(String url) {
        try {
            new URL(url).toURI();
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid URL"
            );
        }
    }

    // =========================
    // GENERATE UNIQUE CODE
    // =========================
    private String generateUniqueCode() {

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();

        String code;

        do {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < 6; i++) {
                sb.append(chars.charAt(random.nextInt(chars.length())));
            }

            code = sb.toString();

        } while (repo.existsByShortCode(code));

        return code;
    }
}
