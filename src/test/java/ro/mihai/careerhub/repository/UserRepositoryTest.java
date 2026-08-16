package ro.mihai.careerhub.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import ro.mihai.careerhub.TestcontainersConfiguration;
import ro.mihai.careerhub.entity.User;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUser() {

        User user = new User(
                "Mihai",
                "Oprea",
                "mihai@testexample.com",
                "password123",
                "0712345678"
        );

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());

        assertEquals(
                "Mihai",
                savedUser.getFirstname()
        );

        assertEquals(
                "Oprea",
                savedUser.getLastname()
        );

        assertEquals(
                "mihai@testexample.com",
                savedUser.getEmail()
        );

        assertEquals(
                "password123",
                savedUser.getPassword()
        );

        assertEquals(
                "0712345678",
                savedUser.getPhonenumber()
        );
    }
}