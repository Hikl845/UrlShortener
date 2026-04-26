package com.example.urlShortener.link;

import com.example.urlShortener.link.dto.LinkResponse;
import com.example.urlShortener.link.dto.LinkStatsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Tag(name = "Links", description = "Operations with short links")
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
    @Operation(summary = "Create short link")
    @PostMapping
    public LinkResponse create(@RequestParam String url) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return service.create(url, username);
    }

    // =========================
    // REDIRECT (PUBLIC)
    // =========================
    @Operation(summary = "Redirect to original URL")
    @GetMapping("/{code}")
    public void redirect(@PathVariable String code,
                         HttpServletResponse response) throws IOException {

        ShortLink link = service.openByCode(code);
        response.sendRedirect(link.getOriginalUrl());
    }

    // =========================
    // GET ALL LINKS
    // =========================
    @Operation(summary = "Get all user links")
    @GetMapping
    public List<LinkResponse> getLinks() {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return service.getUserLinks(username, null);
    }

    // =========================
    // GET ACTIVE LINKS 🔥
    // =========================
    @Operation(summary = "Get active links")
    @GetMapping("/active")
    public List<LinkResponse> getActiveLinks() {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return service.getUserLinks(username, true);
    }

    // =========================
    // DELETE LINK
    // =========================
    @Operation(summary = "Delete link")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        service.delete(id, username);
    }

    // =========================
    // GET STATS
    // =========================
    @Operation(summary = "Get link statistics")
    @GetMapping("/stats/{code}")
    public LinkStatsResponse stats(@PathVariable String code) {
        return service.getStats(code);
    }
}
