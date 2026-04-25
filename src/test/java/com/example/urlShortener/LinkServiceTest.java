package com.example.urlShortener;

import com.example.urlShortener.link.LinkRepository;
import com.example.urlShortener.link.LinkService;
import com.example.urlShortener.link.ShortLink;
import com.example.urlShortener.user.User;
import com.example.urlShortener.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension .class)
class LinkServiceTest {

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LinkService linkService;

    @Test
    void shouldCreateLinkSuccessfully() {

        User user = new User();
        user.setUsername("test");

        when(userRepository.findByUsername("test"))
                .thenReturn(Optional.of(user));

        when(linkRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        ShortLink link = linkService.create("https://google.com", "test");

        assertNotNull(link.getShortCode());
        assertEquals("https://google.com", link.getOriginalUrl());
    }

    @Test
    void shouldThrowIfUserNotFound() {

        when(userRepository.findByUsername("test"))
                .thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () ->
                linkService.create("https://google.com", "test"));
    }

    @Test
    void shouldThrowIfInvalidUrl() {

        assertThrows(ResponseStatusException.class, () ->
                linkService.create("invalid_url", "test"));
    }

    @Test
    void shouldDeleteLink() {

        User user = new User();
        user.setId(1L);

        ShortLink link = new ShortLink();
        link.setId(1L);
        link.setUser(user);

        when(userRepository.findByUsername("test"))
                .thenReturn(Optional.of(user));

        when(linkRepository.findById(1L))
                .thenReturn(Optional.of(link));

        linkService.delete(1L, "test");

        verify(linkRepository).delete(link);
    }

    @Test
    void shouldThrowExceptionWhenDeletingAnotherUsersLink() {

        User user = new User();
        user.setId(1L);

        User other = new User();
        other.setId(2L);

        ShortLink link = new ShortLink();
        link.setId(1L);
        link.setUser(other);

        when(userRepository.findByUsername("test"))
                .thenReturn(Optional.of(user));

        when(linkRepository.findById(1L))
                .thenReturn(Optional.of(link));

        assertThrows(ResponseStatusException.class,
                () -> linkService.delete(1L, "test"));
    }
}