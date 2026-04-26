package com.example.urlShortener;

import com.example.urlShortener.link.LinkRepository;
import com.example.urlShortener.link.LinkServiceImpl;
import com.example.urlShortener.link.ShortLink;
import com.example.urlShortener.link.dto.LinkResponse;
import com.example.urlShortener.link.dto.LinkStatsResponse;
import com.example.urlShortener.user.User;
import com.example.urlShortener.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LinkServiceTest {

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LinkServiceImpl linkService;

    @Test
    void shouldCreateLink() {
        when(userRepository.findByUsername("test"))
                .thenReturn(Optional.of(new User()));

        when(linkRepository.existsByShortCode(anyString()))
                .thenReturn(false);

        LinkResponse response = linkService.create(
                "https://google.com",
                "test"
        );

        assertNotNull(response);
        assertEquals("https://google.com", response.getOriginalUrl());
    }

    @Test
    void shouldOpenLink() {
        ShortLink link = new ShortLink();
        link.setShortCode("abc123");
        link.setOriginalUrl("https://google.com");
        link.setClickCount(0);

        when(linkRepository.findByShortCode("abc123"))
                .thenReturn(Optional.of(link));

        LinkResponse response = linkService.openByCode("abc123");

        assertNotNull(response);
        assertEquals("abc123", response.getShortCode());
    }

    @Test
    void shouldReturnUserLinks() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findByUsername("test"))
                .thenReturn(Optional.of(user));

        when(linkRepository.findAllByUserId(1L))
                .thenReturn(List.of());

        List<LinkResponse> result = linkService.getUserLinks("test");

        assertNotNull(result);
    }

    @Test
    void shouldReturnStats() {
        ShortLink link = new ShortLink();
        link.setShortCode("abc123");
        link.setOriginalUrl("https://google.com");
        link.setClickCount(5);

        when(linkRepository.findByShortCode("abc123"))
                .thenReturn(Optional.of(link));

        LinkStatsResponse stats = linkService.getStats("abc123");

        assertNotNull(stats);
        assertEquals(5, stats.getClickCount());
    }
}