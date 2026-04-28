package com.example.urlshortener.link;

import com.example.urlshortener.link.dto.LinkResponse;
import com.example.urlshortener.link.dto.LinkStatsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/link")
@Tag(name = "Links", description = "Link management API")
public class LinkController {

    private final LinkService service;

    public LinkController(LinkService service) {
        this.service = service;
    }

    @Operation(summary = "Create short link")
    @ApiResponse(responseCode = "200", description = "Link created")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public LinkResponse create(@RequestParam String url) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return service.create(url, username);
    }

    // REDIRECT endpoint (по ТЗ)
    @Operation(summary = "Redirect to original URL")
    @ApiResponse(responseCode = "302", description = "Redirect")
    @GetMapping("/{code}")
    public void redirect(@PathVariable String code,
                         HttpServletResponse response) throws IOException {

        LinkResponse link = service.openByCode(code);
        response.sendRedirect(link.getOriginalUrl());
    }

    @Operation(summary = "Get user links")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public List<LinkResponse> getLinks(@RequestParam(required = false) Boolean active) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return service.getUserLinks(username, active);
    }

    @Operation(summary = "Delete link")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        service.delete(id, username);
    }

    @Operation(summary = "Get link stats")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/stats/{code}")
    public LinkStatsResponse stats(@PathVariable String code) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return service.getStats(code, username);
    }
}