package com.example.urlShortener.link;

import com.example.urlShortener.link.dto.LinkResponse;
import com.example.urlShortener.link.dto.LinkStatsResponse;

import java.util.List;

public interface LinkService {

    LinkResponse create(String url, String username);

    LinkResponse openByCode(String code);

    List<LinkResponse> getUserLinks(String username);

    void delete(Long id, String username);

    //Object getStats(String username);

    LinkStatsResponse getStats(String code);
}