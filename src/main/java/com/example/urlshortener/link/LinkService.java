package com.example.urlshortener.link;

import com.example.urlshortener.link.dto.LinkResponse;
import com.example.urlshortener.link.dto.LinkStatsResponse;

import java.util.List;

public interface LinkService {

    LinkResponse create(String url, String username);

    ShortLink openByCode(String code);

    List<LinkResponse> getUserLinks(String username, Boolean onlyActive);

    void delete(Long id, String username);

    LinkStatsResponse getStats(String code);
}
