package com.example.urlshortener.link;

import com.example.urlshortener.link.dto.LinkResponse;
import com.example.urlshortener.link.dto.LinkStatsResponse;
import com.example.urlshortener.user.User;
import com.example.urlshortener.user.UserRepository;
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

    public LinkServiceImpl(LinkRepository repo, UserRepository userRepository) {
        this.repo = repo;
        this.userRepository = userRepository;
    }

    @Override
    public LinkResponse create(String url, String username) {

        validateUrl(url);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        ShortLink link = new ShortLink();
        link.setOriginalUrl(url);
        link.setShortCode(generateUniqueCode());
        link.setUser(user);
        link.setCreatedAt(LocalDateTime.now());
        link.setExpiresAt(LocalDateTime.now().plusDays(7));
        link.setClickCount(0);

        ShortLink saved = repo.save(link);

        return new LinkResponse(saved.getShortCode(), saved.getOriginalUrl(), saved.getClickCount());
    }

    @Override
    @Transactional
    public ShortLink openByCode(String code) {

        ShortLink link = repo.findByShortCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Link not found"));

        if (link.getExpiresAt() != null && link.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.GONE, "Link expired");
        }

        repo.incrementClick(code);
        return link;
    }

    @Override
    public List<LinkResponse> getUserLinks(String username, Boolean onlyActive) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<ShortLink> links = repo.findAllByUserId(user.getId());

        return links.stream()
                .filter(l -> {
                    if (onlyActive == null) return true;
                    boolean active = l.getExpiresAt() == null || l.getExpiresAt().isAfter(LocalDateTime.now());
                    return onlyActive ? active : true;
                })
                .map(l -> new LinkResponse(l.getShortCode(), l.getOriginalUrl(), l.getClickCount()))
                .toList();
    }

    @Override
    public void delete(Long id, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        ShortLink link = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Link not found"));

        if (!link.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your link");
        }

        repo.delete(link);
    }

    @Override
    public LinkStatsResponse getStats(String code) {

        ShortLink link = repo.findByShortCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Link not found"));

        boolean active = link.getExpiresAt() == null || link.getExpiresAt().isAfter(LocalDateTime.now());

        return new LinkStatsResponse(link.getShortCode(), link.getOriginalUrl(), link.getClickCount(), active);
    }

    private void validateUrl(String url) {
        try {
            if (url == null || url.isBlank()) throw new Exception();
            if (!url.startsWith("http://") && !url.startsWith("https://")) throw new Exception();
            new URL(url).toURI();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid URL");
        }
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
