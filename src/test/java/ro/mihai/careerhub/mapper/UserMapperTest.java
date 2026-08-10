package ro.mihai.careerhub.mapper;

import org.junit.jupiter.api.Test;

import ro.mihai.careerhub.entity.User;
import ro.mihai.careerhub.dto.response.UserResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    void shouldMapUserToResponse() {

        LocalDateTime createdate =
                LocalDateTime.of(2026, 8, 10, 20, 30);

        User user = new User(
                "Mihai",
                "Oprea",
                "mihai.mapper@example.com",
                "password",
                "0770123456"
        );

        user.setCreatedate(createdate);

        UserResponse response = userMapper.toResponse(user);

        assertEquals("Mihai", response.getFirstname());
        assertEquals("Oprea", response.getLastname());
        assertEquals("mihai.mapper@example.com", response.getEmail());
        assertEquals("0770123456", response.getPhonenumber());
        assertEquals(createdate, response.getCreatedate());
    }
}