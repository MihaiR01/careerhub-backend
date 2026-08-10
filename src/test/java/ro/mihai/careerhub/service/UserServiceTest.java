package ro.mihai.careerhub.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ro.mihai.careerhub.dto.request.CreateUserRequest;
import ro.mihai.careerhub.dto.request.UpdateUserRequest;
import ro.mihai.careerhub.dto.response.UserResponse;
import ro.mihai.careerhub.entity.User;
import ro.mihai.careerhub.exception.UserNotFoundException;
import ro.mihai.careerhub.mapper.UserMapper;
import ro.mihai.careerhub.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUser() {

        CreateUserRequest request = new CreateUserRequest(
                "Mihai",
                "Oprea",
                "mihai@example.com",
                "password123",
                "0712345678"
        );

        User user = new User(
                "Mihai",
                "Oprea",
                "mihai@example.com",
                "password123",
                "0712345678"
        );

        LocalDateTime createdate = LocalDateTime.now();
        user.setCreatedate(createdate);

        UserResponse response = new UserResponse(
                1L,
                "Mihai",
                "Oprea",
                "mihai@example.com",
                "0712345678",
                createdate
        );

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(response);

        UserResponse result =
                userService.createUser(request);

        assertEquals(1L, result.getId());
        assertEquals("Mihai", result.getFirstname());
        assertEquals("Oprea", result.getLastname());
        assertEquals("mihai@example.com", result.getEmail());
        assertEquals("0712345678", result.getPhonenumber());
        assertNotNull(result.getCreatedate());
        assertEquals(createdate, result.getCreatedate());

        verify(userRepository).save(any(User.class));
        verify(userMapper).toResponse(user);
    }

    @Test
    void shouldGetAllUsers() {

        LocalDateTime createdate1 =
                LocalDateTime.of(2026, 8, 10, 20, 30);

        LocalDateTime createdate2 =
                LocalDateTime.of(2026, 8, 11, 10, 15);

        User user1 = new User(
                "Mihai",
                "Oprea",
                "mihai.one@example.com",
                "password123",
                "0712345678"
        );

        user1.setCreatedate(createdate1);

        User user2 = new User(
                "John",
                "Doe",
                "john.doe@example.com",
                "password456",
                "0723456789"
        );

        user2.setCreatedate(createdate2);

        UserResponse response1 = new UserResponse(
                1L,
                "Mihai",
                "Oprea",
                "mihai.one@example.com",
                "0712345678",
                createdate1
        );

        UserResponse response2 = new UserResponse(
                2L,
                "John",
                "Doe",
                "john.doe@example.com",
                "0723456789",
                createdate2
        );

        when(userRepository.findAll())
                .thenReturn(List.of(user1, user2));

        when(userMapper.toResponse(user1))
                .thenReturn(response1);

        when(userMapper.toResponse(user2))
                .thenReturn(response2);

        List<UserResponse> result =
                userService.getAllUsers();

        assertEquals(2, result.size());

        assertEquals(
                "Mihai",
                result.get(0).getFirstname()
        );

        assertEquals(
                "Oprea",
                result.get(0).getLastname()
        );

        assertEquals(
                "mihai.one@example.com",
                result.get(0).getEmail()
        );

        assertEquals(
                createdate1,
                result.get(0).getCreatedate()
        );

        assertEquals(
                "John",
                result.get(1).getFirstname()
        );

        assertEquals(
                "Doe",
                result.get(1).getLastname()
        );

        assertEquals(
                "john.doe@example.com",
                result.get(1).getEmail()
        );

        assertEquals(
                createdate2,
                result.get(1).getCreatedate()
        );

        verify(userRepository).findAll();
        verify(userMapper).toResponse(user1);
        verify(userMapper).toResponse(user2);
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoUsers() {

        when(userRepository.findAll())
                .thenReturn(List.of());

        List<UserResponse> result =
                userService.getAllUsers();

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(userRepository).findAll();
        verifyNoInteractions(userMapper);
    }

    @Test
    void shouldGetUserById() {

        LocalDateTime createdate =
                LocalDateTime.of(2026, 8, 11, 10, 30);

        User user = new User(
                "Mihai",
                "Oprea",
                "mihai.get@example.com",
                "password123",
                "0712345678"
        );

        user.setCreatedate(createdate);

        UserResponse response = new UserResponse(
                1L,
                "Mihai",
                "Oprea",
                "mihai.get@example.com",
                "0712345678",
                createdate
        );

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userMapper.toResponse(user))
                .thenReturn(response);

        UserResponse result =
                userService.getUserById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Mihai", result.getFirstname());
        assertEquals("Oprea", result.getLastname());
        assertEquals("mihai.get@example.com", result.getEmail());
        assertEquals("0712345678", result.getPhonenumber());
        assertEquals(createdate, result.getCreatedate());

        verify(userRepository).findById(1L);
        verify(userMapper).toResponse(user);
    }

    @Test
    void shouldThrowUserNotFoundException() {

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserById(999L)
        );

        verify(userRepository).findById(999L);
        verifyNoInteractions(userMapper);
    }

    @Test
    void shouldDeleteUser() {

        User user = new User(
                "Mihai",
                "Oprea",
                "mihai.delete@example.com",
                "password123",
                "0712345678"
        );

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).findById(1L);
        verify(userRepository).delete(user);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenDeletingUser() {

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.deleteUser(999L)
        );

        verify(userRepository).findById(999L);
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void shouldUpdateUser() {

        LocalDateTime createdate =
                LocalDateTime.of(2026, 8, 1, 12, 30);

        User user = new User(
                "Mihai",
                "Oprea",
                "mihai@example.com",
                "oldPassword",
                "0712345678"
        );

        user.setCreatedate(createdate);

        UpdateUserRequest request = new UpdateUserRequest();

        request.setFirstname("Robert");
        request.setLastname("Oprea");
        request.setEmail("robert@example.com");
        request.setPassword("newPassword");
        request.setPhonenumber("0798765432");

        UserResponse response = new UserResponse(
                1L,
                "Robert",
                "Oprea",
                "robert@example.com",
                "0798765432",
                createdate
        );

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(response);

        UserResponse result =
                userService.updateUser(1L, request);

        assertEquals("Robert", result.getFirstname());
        assertEquals("Oprea", result.getLastname());
        assertEquals("robert@example.com", result.getEmail());
        assertEquals("0798765432", result.getPhonenumber());

        assertEquals(
                "newPassword",
                user.getPassword()
        );

        assertEquals(
                createdate,
                result.getCreatedate()
        );

        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUpdatingUser() {

        UpdateUserRequest request = new UpdateUserRequest();

        request.setFirstname("Robert");
        request.setLastname("Oprea");
        request.setEmail("robert@example.com");
        request.setPassword("newPassword");
        request.setPhonenumber("0798765432");

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.updateUser(999L, request)
        );

        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(userMapper);
    }
}