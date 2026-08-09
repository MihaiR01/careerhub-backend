package ro.mihai.careerhub.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ro.mihai.careerhub.dto.request.CreateUserRequest;
import ro.mihai.careerhub.dto.response.UserResponse;
import ro.mihai.careerhub.entity.User;
import ro.mihai.careerhub.mapper.UserMapper;
import ro.mihai.careerhub.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        UserResponse response = new UserResponse(
                1L,
                "Mihai",
                "Oprea",
                "mihai@example.com",
                "0712345678"
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

        verify(userRepository).save(any(User.class));
        verify(userMapper).toResponse(user);
    }
}