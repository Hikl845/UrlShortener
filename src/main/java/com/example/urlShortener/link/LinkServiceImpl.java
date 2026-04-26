package com.example.urlshortener.link;

import com.example.urlShortener.exception.BadRequestException;
import com.example.urlShortener.link.dto.LinkResponse;
import com.example.urlShortener.link.dto.LinkStatsResponse;
import com.example.urlShortener.user.User;
import com.example.urlShortener.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class LinkServiceImpl implements LinkService {

    private final LinkRepository repo;
    private final UserRepository userRepository;

    public LinkServiceImpl(LinkRepository repo, UserRepository userRepository) {
        this.repo = repo;
        this.userRepository = userRepository;
    }

    // =========================
    // CREATE
    // =========================
    @Override
    public LinkResponse create(String url, String username) {

        validateUrl(url);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found"));

        ShortLink link = new ShortLink();
        link.setOriginalUrl(url);
        link.setShortCode(generateUniqueCode());
        link.setUser(user);
        link.setCreatedAt(LocalDateTime.now());
        link.setExpiresAt(LocalDateTime.now().plusDays(7));
        link.setClickCount(0);

        repo.save(link);

        return mapToResponse(link);
    }

    // =========================
    // OPEN
    // =========================
    @Override
    @Transactional
    public LinkResponse openByCode(String code) {

        ShortLink link = repo.findByShortCode(code)
                .orElseThrow(() -> new BadRequestException("Link not found"));

        if (link.getExpiresAt() != null &&
                link.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Link expired");
        }

        repo.incrementClick(code);

        return mapToResponse(link);
    }

    // =========================
    // GET USER LINKS
    // =========================
    @Override
    public List<LinkResponse> getUserLinks(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found"));

        return repo.findAllByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =========================
    // DELETE
    // =========================
    @Override
    public void delete(Long id, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found"));

        ShortLink link = repo.findById(id)
                .orElseThrow(() -> new BadRequestException("Link not found"));

        if (!link.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Not your link");
        }

        repo.delete(link);
    }

    // =========================
    // STATS
    // =========================
    @Override
    public LinkStatsResponse getStats(String code) {

        ShortLink link = repo.findByShortCode(code)
                .orElseThrow(() -> new BadRequestException("Link not found"));

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
    // VALIDATION
    // =========================
    private void validateUrl(String url) {

        if (url == null || url.isBlank()) {
            throw new BadRequestException("URL is empty");
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new BadRequestException("Invalid protocol");
        }

        try {
            new URL(url).toURI();
        } catch (Exception e) {
            throw new BadRequestException("Invalid URL format");
        }
    }

    // =========================
    // MAPPER
    // =========================
    private LinkResponse mapToResponse(ShortLink link) {
        return new LinkResponse(
                link.getShortCode(),
                link.getOriginalUrl(),
                link.getClickCount()
        );
    }

    // =========================
    // CODE GENERATOR
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
