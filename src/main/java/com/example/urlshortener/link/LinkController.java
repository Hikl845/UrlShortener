package com.example.urlshortener.link;

import com.example.urlshortener.link.dto.LinkResponse;
import com.example.urlshortener.link.dto.LinkStatsResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/link")
public class LinkController {

    private final LinkService service;

    public LinkController(LinkService service) {
        this.service = service;
    }

    @PostMapping
    public LinkResponse create(@RequestParam String url) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return service.create(url, username);
    }

    @GetMapping("/{code}")
    public LinkResponse open(@PathVariable String code) {
        return service.openByCode(code);
    }

    @GetMapping
    public List<LinkResponse> getLinks(
            @RequestParam(required = false) Boolean active
    ) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return service.getUserLinks(username, active);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        service.delete(id, username);
    }

    @GetMapping("/stats/{code}")
    public LinkStatsResponse stats(@PathVariable String code) {
        return service.getStats(code);
    }
}
