package ro.mihai.careerhub.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import ro.mihai.careerhub.dto.request.CreateUserRequest;
import ro.mihai.careerhub.dto.request.UpdateUserRequest;
import ro.mihai.careerhub.dto.response.UserResponse;
import ro.mihai.careerhub.entity.User;
import ro.mihai.careerhub.enums.Role;
import ro.mihai.careerhub.exception.UserNotFoundException;
import ro.mihai.careerhub.mapper.UserMapper;
import ro.mihai.careerhub.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

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
                "encoded-password",
                "0712345678"
        );

        LocalDateTime createdate =
                LocalDateTime.of(2026, 8, 15, 18, 30);

        user.setId(1L);
        user.setCreatedate(createdate);
        user.setRole(Role.USER);

        UserResponse response = new UserResponse(
                1L,
                "Mihai",
                "Oprea",
                "mihai@example.com",
                "0712345678",
                createdate
        );

        when(passwordEncoder.encode("password123"))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(response);

        UserResponse result =
                userService.createUser(request);

        assertEquals(1L, result.getId());
        assertEquals("Mihai", result.getFirstname());
        assertEquals("Oprea", result.getLastname());
        assertEquals(
                "mihai@example.com",
                result.getEmail()
        );
        assertEquals(
                "0712345678",
                result.getPhonenumber()
        );

        assertEquals(
                "encoded-password",
                user.getPassword()
        );

        assertEquals(
                Role.USER,
                user.getRole()
        );

        verify(passwordEncoder)
                .encode("password123");

        verify(userRepository)
                .save(any(User.class));

        verify(userMapper)
                .toResponse(user);
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
                "encoded-password-1",
                "0712345678"
        );

        user1.setId(1L);
        user1.setCreatedate(createdate1);
        user1.setRole(Role.USER);

        User user2 = new User(
                "John",
                "Doe",
                "john.doe@example.com",
                "encoded-password-2",
                "0723456789"
        );

        user2.setId(2L);
        user2.setCreatedate(createdate2);
        user2.setRole(Role.USER);

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
                "John",
                result.get(1).getFirstname()
        );

        verify(userRepository)
                .findAll();

        verify(userMapper)
                .toResponse(user1);

        verify(userMapper)
                .toResponse(user2);
    }

    @Test
    void shouldGetOwnUserById() {

        User user = new User(
                "Mihai",
                "Oprea",
                "mihai@example.com",
                "encoded-password",
                "0712345678"
        );

        user.setId(1L);
        user.setRole(Role.USER);

        UserResponse response = new UserResponse(
                1L,
                "Mihai",
                "Oprea",
                "mihai@example.com",
                "0712345678",
                user.getCreatedate()
        );

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userMapper.toResponse(user))
                .thenReturn(response);

        UserResponse result =
                userService.getUserById(
                        1L,
                        "mihai@example.com",
                        false
                );

        assertEquals(
                1L,
                result.getId()
        );

        assertEquals(
                "mihai@example.com",
                result.getEmail()
        );

        verify(userRepository)
                .findById(1L);

        verify(userMapper)
                .toResponse(user);
    }

    @Test
    void shouldAllowAdminToGetAnotherUser() {

        User user = new User(
                "John",
                "Doe",
                "john@example.com",
                "encoded-password",
                "0723456789"
        );

        user.setId(2L);
        user.setRole(Role.USER);

        UserResponse response = new UserResponse(
                2L,
                "John",
                "Doe",
                "john@example.com",
                "0723456789",
                user.getCreatedate()
        );

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(user));

        when(userMapper.toResponse(user))
                .thenReturn(response);

        UserResponse result =
                userService.getUserById(
                        2L,
                        "admin@example.com",
                        true
                );

        assertEquals(2L, result.getId());
        assertEquals(
                "john@example.com",
                result.getEmail()
        );

        verify(userRepository)
                .findById(2L);

        verify(userMapper)
                .toResponse(user);
    }

    @Test
    void shouldDenyUserAccessToAnotherUser() {

        User user = new User(
                "John",
                "Doe",
                "john@example.com",
                "encoded-password",
                "0723456789"
        );

        user.setId(2L);
        user.setRole(Role.USER);

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(user));

        assertThrows(
                AccessDeniedException.class,
                () -> userService.getUserById(
                        2L,
                        "mihai@example.com",
                        false
                )
        );

        verify(userRepository)
                .findById(2L);

        verifyNoInteractions(userMapper);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenGettingUserById() {

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserById(
                        999L,
                        "mihai@example.com",
                        false
                )
        );

        verify(userRepository)
                .findById(999L);

        verifyNoInteractions(userMapper);
    }

    @Test
    void shouldUpdateOwnUser() {

        User user = new User(
                "Mihai",
                "Oprea",
                "mihai@example.com",
                "old-password",
                "0712345678"
        );

        user.setId(1L);
        user.setRole(Role.USER);

        UpdateUserRequest request =
                new UpdateUserRequest();

        request.setFirstname("Robert");
        request.setLastname("Oprea");
        request.setEmail("mihai.new@example.com");
        request.setPassword("newPassword");
        request.setPhonenumber("0798765432");

        UserResponse response = new UserResponse(
                1L,
                "Robert",
                "Oprea",
                "mihai.new@example.com",
                "0798765432",
                user.getCreatedate()
        );

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode("newPassword"))
                .thenReturn("encoded-new-password");

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(response);

        UserResponse result =
                userService.updateUser(
                        1L,
                        request,
                        "mihai@example.com",
                        false
                );

        assertEquals(
                "Robert",
                result.getFirstname()
        );

        assertEquals(
                "mihai.new@example.com",
                result.getEmail()
        );

        assertEquals(
                "encoded-new-password",
                user.getPassword()
        );

        verify(passwordEncoder)
                .encode("newPassword");

        verify(userRepository)
                .save(user);

        verify(userMapper)
                .toResponse(user);
    }

    @Test
    void shouldAllowAdminToUpdateAnotherUser() {

        User user = new User(
                "John",
                "Doe",
                "john@example.com",
                "old-password",
                "0723456789"
        );

        user.setId(2L);
        user.setRole(Role.USER);

        UpdateUserRequest request =
                new UpdateUserRequest();

        request.setFirstname("Johnny");
        request.setLastname("Doe");
        request.setEmail("johnny@example.com");
        request.setPassword("newPassword");
        request.setPhonenumber("0799999999");

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode("newPassword"))
                .thenReturn("encoded-new-password");

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(
                        new UserResponse(
                                2L,
                                "Johnny",
                                "Doe",
                                "johnny@example.com",
                                "0799999999",
                                user.getCreatedate()
                        )
                );

        UserResponse result =
                userService.updateUser(
                        2L,
                        request,
                        "admin@example.com",
                        true
                );

        assertEquals(
                "Johnny",
                result.getFirstname()
        );

        verify(userRepository)
                .save(user);
    }

    @Test
    void shouldDenyUserUpdatingAnotherUser() {

        User user = new User(
                "John",
                "Doe",
                "john@example.com",
                "old-password",
                "0723456789"
        );

        user.setId(2L);
        user.setRole(Role.USER);

        UpdateUserRequest request =
                new UpdateUserRequest();

        request.setFirstname("Hacked");
        request.setLastname("User");
        request.setEmail("hacked@example.com");
        request.setPassword("newPassword");
        request.setPhonenumber("0700000000");

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(user));

        assertThrows(
                AccessDeniedException.class,
                () -> userService.updateUser(
                        2L,
                        request,
                        "mihai@example.com",
                        false
                )
        );

        verify(userRepository)
                .findById(2L);

        verify(
                userRepository,
                never()
        ).save(any(User.class));

        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(userMapper);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUpdatingUser() {

        UpdateUserRequest request =
                new UpdateUserRequest();

        request.setFirstname("Robert");
        request.setLastname("Oprea");
        request.setEmail("robert@example.com");
        request.setPassword("newPassword");
        request.setPhonenumber("0798765432");

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.updateUser(
                        999L,
                        request,
                        "mihai@example.com",
                        false
                )
        );

        verify(userRepository)
                .findById(999L);

        verify(
                userRepository,
                never()
        ).save(any(User.class));

        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(userMapper);
    }

    @Test
    void shouldAllowAdminToDeleteAnotherUser() {

        User user = new User(
                "John",
                "Doe",
                "john.delete@example.com",
                "encoded-password",
                "0723456789"
        );

        user.setId(2L);
        user.setRole(Role.USER);

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(user));

        userService.deleteUser(
                2L,
                "admin@example.com",
                true
        );

        verify(userRepository)
                .findById(2L);

        verify(userRepository)
                .delete(user);
    }

    @Test
    void shouldDenyUserDeletingAnotherUser() {

        User user = new User(
                "John",
                "Doe",
                "john.delete2@example.com",
                "encoded-password",
                "0723456789"
        );

        user.setId(2L);
        user.setRole(Role.USER);

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(user));

        assertThrows(
                AccessDeniedException.class,
                () -> userService.deleteUser(
                        2L,
                        "mihai@example.com",
                        false
                )
        );

        verify(userRepository)
                .findById(2L);

        verify(
                userRepository,
                never()
        ).delete(any(User.class));
    }

    @Test
    void shouldDeleteOwnUser() {

        User user = new User(
                "Mihai",
                "Oprea",
                "mihai.delete@example.com",
                "encoded-password",
                "0712345678"
        );

        user.setId(1L);
        user.setRole(Role.USER);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        userService.deleteUser(
                1L,
                "mihai.delete@example.com",
                false
        );

        verify(userRepository)
                .findById(1L);

        verify(userRepository)
                .delete(user);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenDeletingUser() {

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.deleteUser(
                        999L,
                        "mihai@example.com",
                        false
                )
        );

        verify(userRepository)
                .findById(999L);

        verify(
                userRepository,
                never()
        ).delete(any(User.class));
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoUsers() {

        when(userRepository.findAll())
                .thenReturn(List.of());

        List<UserResponse> result =
                userService.getAllUsers();

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(userRepository)
                .findAll();

        verifyNoInteractions(userMapper);
    }
}