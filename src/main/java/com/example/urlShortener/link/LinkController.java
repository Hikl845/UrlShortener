package com.example.urlShortener.link;

import com.example.urlShortener.link.dto.LinkResponse;
import com.example.urlShortener.link.dto.LinkStatsResponse;
import com.example.urlShortener.user.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/link")
public class LinkController {

    private final LinkService service;

    public LinkController(LinkService service) {
        this.service = service;
    }

    // =========================
    // CREATE LINK
    // =========================
    @PostMapping
    public LinkResponse create(@RequestParam String url,
                               @AuthenticationPrincipal User user) {

        return service.create(url, user.getUsername());
    }

    // =========================
    // REDIRECT (PUBLIC)
    // =========================
    @GetMapping("/{code}")
    public void redirect(@PathVariable String code,
                         HttpServletResponse response) throws IOException {

        LinkResponse link = service.openByCode(code);
        response.sendRedirect(link.getOriginalUrl());
    }

    // =========================
    // GET USER LINKS
    // =========================
    @GetMapping
    public List<LinkResponse> getLinks(@AuthenticationPrincipal User user) {

        return service.getUserLinks(user.getUsername());
    }

    // =========================
    // DELETE LINK
    // =========================
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id,
                       @AuthenticationPrincipal User user) {

        service.delete(id, user.getUsername());
    }

    // =========================
    // STATS
    // =========================
    @GetMapping("/stats/{code}")
    public LinkStatsResponse stats(@PathVariable String code) {

        return service.getStats(code);
    }
}