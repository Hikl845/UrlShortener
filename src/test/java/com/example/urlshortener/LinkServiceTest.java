package com.example.urlshortener;

import com.example.urlshortener.link.*;
import com.example.urlshortener.link.dto.LinkResponse;
import com.example.urlshortener.link.dto.LinkStatsResponse;
import com.example.urlshortener.user.User;
import com.example.urlshortener.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

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
        User user = new User();
        user.setId(1L);
        user.setUsername("test");

        when(userRepository.findByUsername("test"))
                .thenReturn(Optional.of(user));

        when(linkRepository.existsByShortCode(anyString()))
                .thenReturn(false);

        when(linkRepository.save(any(ShortLink.class)))
                .thenAnswer(invocation -> {
                    ShortLink link = invocation.getArgument(0);
                    link.setShortCode("abc123");
                    return link;
                });

        LinkResponse response = linkService.create(
                "https://google.com",
                "test"
        );

        assertNotNull(response);
        assertEquals("https://google.com", response.getOriginalUrl());
        assertEquals("abc123", response.getShortCode());
    }

    @Test
    void shouldOpenLink() {
        ShortLink link = new ShortLink();
        link.setShortCode("abc123");
        link.setOriginalUrl("https://google.com");
        link.setClickCount(0);
        link.setExpiresAt(LocalDateTime.now().plusDays(1)); // щоб не було expired

        when(linkRepository.findByShortCode("abc123"))
                .thenReturn(Optional.of(link));

        LinkResponse response = linkService.openByCode("abc123");

        assertNotNull(response);
        assertEquals("abc123", response.getShortCode());

        verify(linkRepository).incrementClick("abc123");
    }

    @Test
    void shouldReturnUserLinks() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");

        when(userRepository.findByUsername("test"))
                .thenReturn(Optional.of(user));

        when(linkRepository.findAllByUserId(1L))
                .thenReturn(List.of());

        List<LinkResponse> result = linkService.getUserLinks("test", false);

        assertNotNull(result);
    }

    @Test
    void shouldReturnStats() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");

        ShortLink link = new ShortLink();
        link.setShortCode("abc123");
        link.setOriginalUrl("https://google.com");
        link.setClickCount(5);
        link.setUser(user);
        link.setExpiresAt(LocalDateTime.now().plusDays(1));

        when(userRepository.findByUsername("test"))
                .thenReturn(Optional.of(user));

        when(linkRepository.findByShortCode("abc123"))
                .thenReturn(Optional.of(link));

        LinkStatsResponse stats = linkService.getStats("abc123", "test");

        assertNotNull(stats);
        assertEquals(5, stats.getClickCount());
        assertTrue(stats.isActive());
    }
}