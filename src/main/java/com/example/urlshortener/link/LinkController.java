package com.example.urlshortener.link;

import com.example.urlshortener.link.dto.LinkResponse;
import com.example.urlshortener.link.dto.LinkStatsResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
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
    // CREATE
    // =========================
    @PostMapping
    public LinkResponse create(@RequestParam String url) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return service.create(url, username);
    }

    // =========================
    // REDIRECT (🔥 FIX)
    // =========================
    @GetMapping("/{code}")
    public void open(@PathVariable String code,
                     HttpServletResponse response) throws IOException {

        LinkResponse link = service.openByCode(code);

        response.sendRedirect(link.getOriginalUrl());
    }

    // =========================
    // GET LINKS (з фільтром active)
    // =========================
    @GetMapping
    public List<LinkResponse> getLinks(
            @RequestParam(required = false) Boolean active
    ) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return service.getUserLinks(username, active);
    }

    // =========================
    // DELETE
    // =========================
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        service.delete(id, username);
    }

    // =========================
    // STATS
    // =========================
    @GetMapping("/stats/{code}")
    public LinkStatsResponse stats(@PathVariable String code) {
        return service.getStats(code);
    }
}
