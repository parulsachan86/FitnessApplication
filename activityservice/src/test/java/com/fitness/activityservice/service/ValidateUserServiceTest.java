package com.fitness.activityservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidateUserServiceTest {

    @Mock
    private WebClient userServiceWebClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private Mono<Boolean> mono;

    @InjectMocks
    private ValidateUserService validateUserService;

    @BeforeEach
    void setUp() {
        when(userServiceWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), (Object) any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(mono);
        when(mono.timeout(Duration.ofSeconds(2))).thenReturn(mono);
    }

    @Test
    void validateUser_shouldReturnTrue_whenUserIsValid() {
        when(mono.block()).thenReturn(true);

        boolean result = validateUserService.validateUser("1");

        assertTrue(result);
    }

    @Test
    void validateUser_shouldReturnFalse_whenUserIsInvalid() {
        when(mono.block()).thenReturn(false);

        boolean result = validateUserService.validateUser("1");

        assertFalse(result);
    }

    @Test
    void validateUser_shouldReturnFalse_whenResponseIsNull() {
        when(mono.block()).thenReturn(null);

        boolean result = validateUserService.validateUser("1");

        assertFalse(result);
    }

    @Test
    void validateUser_shouldReturnFalse_when404() {
        WebClientResponseException exception = WebClientResponseException.create(404, "Not Found", null, null, null);
        when(mono.block()).thenThrow(exception);

        boolean result = validateUserService.validateUser("1");

        assertFalse(result);
    }

    @Test
    void validateUser_shouldReturnFalse_when500() {
        WebClientResponseException exception = WebClientResponseException.create(500, "Internal Server Error", null, null, null);
        when(mono.block()).thenThrow(exception);

        boolean result = validateUserService.validateUser("1");

        assertFalse(result);
    }

    @Test
    void validateUser_shouldThrowRuntimeException_whenOtherStatus() {
        WebClientResponseException exception = WebClientResponseException.create(400, "Bad Request", null, null, null);
        when(mono.block()).thenThrow(exception);

        assertThrows(RuntimeException.class, () -> validateUserService.validateUser("1"));
    }
}
