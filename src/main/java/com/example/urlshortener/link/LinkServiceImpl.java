package com.example.urlshortener.link;

import com.example.urlshortener.exception.BadRequestException;
import com.example.urlshortener.link.dto.LinkResponse;
import com.example.urlshortener.link.dto.LinkStatsResponse;
import com.example.urlshortener.user.User;
import com.example.urlshortener.user.UserRepository;
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

    @Override
    public List<LinkResponse> getUserLinks(String username, Boolean active) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found"));

        List<ShortLink> links;

        if (Boolean.TRUE.equals(active)) {
            links = repo.findAllByUserIdAndExpiresAtAfter(
                    user.getId(),
                    LocalDateTime.now()
            );
        } else {
            links = repo.findAllByUserId(user.getId());
        }

        return links.stream()
                .map(this::mapToResponse)
                .toList();
    }

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

    @Override
    public LinkStatsResponse getStats(String code, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found"));

        ShortLink link = repo.findByShortCode(code)
                .orElseThrow(() -> new BadRequestException("Link not found"));

        if (!link.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Access denied");
        }

        boolean active = link.getExpiresAt() == null ||
                link.getExpiresAt().isAfter(LocalDateTime.now());

        return new LinkStatsResponse(
                link.getShortCode(),
                link.getOriginalUrl(),
                link.getClickCount(),
                active
        );
    }

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

    private LinkResponse mapToResponse(ShortLink link) {
        return new LinkResponse(
                link.getShortCode(),
                link.getOriginalUrl(),
                link.getClickCount()
        );
    }

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