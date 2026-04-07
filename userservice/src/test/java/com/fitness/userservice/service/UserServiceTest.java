package com.fitness.userservice.service;

import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserReponse;
import com.fitness.userservice.model.User;
import com.fitness.userservice.model.UserRole;
import com.fitness.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void register_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setFirstName("John");
        request.setLastName("Doe");

        User savedUser = new User();
        savedUser.setId("1");
        savedUser.setEmail("test@example.com");
        savedUser.setPassword("password123");
        savedUser.setFirstName("John");
        savedUser.setLastName("Doe");
        savedUser.setRole(UserRole.USER);
        savedUser.setCreatedAt(LocalDateTime.now());
        savedUser.setUpdateAt(LocalDateTime.now());

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserReponse response = userService.register(request);

        assertNotNull(response);
        assertEquals("1", response.getId());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("password123", response.getPassword());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        verify(userRepository, times(1)).existsByEmail("test@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_EmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.register(request));
        assertEquals("Email Already exist", exception.getMessage());
        verify(userRepository, times(1)).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserDetails_Success() {
        User user = new User();
        user.setId("1");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdateAt(LocalDateTime.now());

        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        UserReponse response = userService.getUserDetails("1");

        assertNotNull(response);
        assertEquals("1", response.getId());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("password123", response.getPassword());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        verify(userRepository, times(1)).findById("1");
    }

    @Test
    void getUserDetails_UserNotFound() {
        when(userRepository.findById("1")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getUserDetails("1"));
        assertEquals(" User does not exist with userId:1", exception.getMessage());
        verify(userRepository, times(1)).findById("1");
    }

    @Test
    void existByUserId_True() {
        when(userRepository.existsById("1")).thenReturn(true);

        Boolean result = userService.existByUserId("1");

        assertTrue(result);
        verify(userRepository, times(1)).existsById("1");
    }

    @Test
    void existByUserId_False() {
        when(userRepository.existsById("1")).thenReturn(false);

        Boolean result = userService.existByUserId("1");

        assertFalse(result);
        verify(userRepository, times(1)).existsById("1");
    }
}
