package ro.mihai.careerhub.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import ro.mihai.careerhub.entity.User;
import ro.mihai.careerhub.enums.Role;
import ro.mihai.careerhub.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        userDetailsService =
                new CustomUserDetailsService(
                        userRepository
                );
    }

    @Test
    void shouldLoadUserByEmail() {

        User user = new User(
                "Mihai",
                "Oprea",
                "mihai@example.com",
                "{bcrypt}encoded-password",
                "0712345678"
        );

        user.setRole(Role.USER);

        when(userRepository.findByEmail("mihai@example.com"))
                .thenReturn(Optional.of(user));

        UserDetails result =
                userDetailsService.loadUserByUsername(
                        "mihai@example.com"
                );

        assertEquals(
                "mihai@example.com",
                result.getUsername()
        );

        assertEquals(
                "{bcrypt}encoded-password",
                result.getPassword()
        );

        assertTrue(
                result.getAuthorities()
                        .stream()
                        .anyMatch(
                                authority ->
                                        authority.getAuthority()
                                                .equals("ROLE_USER")
                        )
        );

        verify(userRepository)
                .findByEmail("mihai@example.com");
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {

        when(userRepository.findByEmail(
                "missing@example.com"
        ))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService
                        .loadUserByUsername(
                                "missing@example.com"
                        )
        );

        verify(userRepository)
                .findByEmail("missing@example.com");
    }
}