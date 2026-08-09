package ro.mihai.careerhub.mapper;

import org.junit.jupiter.api.Test;

import ro.mihai.careerhub.entity.User;
import ro.mihai.careerhub.dto.response.UserResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    void shouldMapUserToResponse() {

        User user = new User(
                "Mihai",
                "Oprea",
                "mihai@testexample.com",
                "password123",
                "0712345678"
        );

        UserResponse response = userMapper.toResponse(user);

        assertEquals("Mihai", response.getFirstname());
        assertEquals("Oprea", response.getLastname());
        assertEquals(
                "mihai@testexample.com",
                response.getEmail()
        );
        assertEquals("0712345678", response.getPhonenumber());
    }
}