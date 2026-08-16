package ro.mihai.careerhub.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ro.mihai.careerhub.config.JwtConfig;
import ro.mihai.careerhub.config.PasswordConfig;
import ro.mihai.careerhub.config.SecurityConfig;
import ro.mihai.careerhub.dto.request.CreateUserRequest;
import ro.mihai.careerhub.dto.request.UpdateUserRequest;
import ro.mihai.careerhub.dto.response.UserResponse;
import ro.mihai.careerhub.service.CustomUserDetailsService;
import ro.mihai.careerhub.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import({
        SecurityConfig.class,
        PasswordConfig.class,
        JwtConfig.class
})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor userJwt(
            String email) {

        Jwt jwt = Jwt.withTokenValue("test-user-token")
                .header("alg", "none")
                .subject(email)
                .build();

        return jwt()
                .jwt(jwt)
                .authorities(
                        new SimpleGrantedAuthority("ROLE_USER")
                );
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor adminJwt(
            String email) {

        Jwt jwt = Jwt.withTokenValue("test-admin-token")
                .header("alg", "none")
                .subject(email)
                .build();

        return jwt()
                .jwt(jwt)
                .authorities(
                        new SimpleGrantedAuthority("ROLE_ADMIN")
                );
    }

    // =========================================================
    // CREATE USER
    // =========================================================

    @Test
    void shouldCreateUserWithoutAuthentication()
            throws Exception {

        LocalDateTime createdate =
                LocalDateTime.of(2026, 8, 15, 18, 30);

        UserResponse response =
                new UserResponse(
                        1L,
                        "Mihai",
                        "Oprea",
                        "mihai@example.com",
                        "0712345678",
                        createdate
                );

        when(
                userService.createUser(
                        any(CreateUserRequest.class)
                )
        ).thenReturn(response);

        mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstname": "Mihai",
                                    "lastname": "Oprea",
                                    "email": "mihai@example.com",
                                    "password": "password123",
                                    "phonenumber": "0712345678"
                                }
                                """)
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.firstname")
                .value("Mihai"))
        .andExpect(jsonPath("$.lastname")
                .value("Oprea"))
        .andExpect(jsonPath("$.email")
                .value("mihai@example.com"))
        .andExpect(jsonPath("$.phonenumber")
                .value("0712345678"));
    }

    // =========================================================
    // GET ALL USERS
    // =========================================================

    @Test
    void shouldReturn403WhenUserGetsAllUsers()
            throws Exception {

        mockMvc.perform(
                get("/users")
                        .with(userJwt("mihai@example.com"))
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToGetAllUsers()
            throws Exception {

        LocalDateTime createdate =
                LocalDateTime.of(2026, 8, 15, 18, 30);

        UserResponse response1 =
                new UserResponse(
                        1L,
                        "Mihai",
                        "Oprea",
                        "mihai@example.com",
                        "0712345678",
                        createdate
                );

        UserResponse response2 =
                new UserResponse(
                        2L,
                        "John",
                        "Doe",
                        "john@example.com",
                        "0723456789",
                        createdate
                );

        when(userService.getAllUsers())
                .thenReturn(
                        List.of(response1, response2)
                );

        mockMvc.perform(
                get("/users")
                        .with(
                                adminJwt("admin@example.com")
                        )
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].email")
                .value("mihai@example.com"))
        .andExpect(jsonPath("$[1].id").value(2))
        .andExpect(jsonPath("$[1].email")
                .value("john@example.com"));
    }

    // =========================================================
    // GET USER BY ID
    // =========================================================

    @Test
    void shouldAllowUserToGetOwnUser()
            throws Exception {

        LocalDateTime createdate =
                LocalDateTime.of(2026, 8, 15, 18, 30);

        UserResponse response =
                new UserResponse(
                        1L,
                        "Mihai",
                        "Oprea",
                        "mihai@example.com",
                        "0712345678",
                        createdate
                );

        when(
                userService.getUserById(
                        1L,
                        "mihai@example.com",
                        false
                )
        ).thenReturn(response);

        mockMvc.perform(
                get("/users/1")
                        .with(
                                userJwt("mihai@example.com")
                        )
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.email")
                .value("mihai@example.com"));
    }

    @Test
    void shouldReturn403WhenUserGetsAnotherUser()
            throws Exception {

        when(
                userService.getUserById(
                        2L,
                        "mihai@example.com",
                        false
                )
        ).thenThrow(
                new org.springframework.security.access.AccessDeniedException(
                        "You do not have permission"
                )
        );

        mockMvc.perform(
                get("/users/2")
                        .with(
                                userJwt("mihai@example.com")
                        )
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToGetAnotherUser()
            throws Exception {

        UserResponse response =
                new UserResponse(
                        2L,
                        "John",
                        "Doe",
                        "john@example.com",
                        "0723456789",
                        null
                );

        when(
                userService.getUserById(
                        2L,
                        "admin@example.com",
                        true
                )
        ).thenReturn(response);

        mockMvc.perform(
                get("/users/2")
                        .with(
                                adminJwt("admin@example.com")
                        )
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(2))
        .andExpect(jsonPath("$.email")
                .value("john@example.com"));
    }

    // =========================================================
    // UPDATE USER
    // =========================================================

    @Test
    void shouldAllowUserToUpdateOwnUser()
            throws Exception {

        LocalDateTime createdate =
                LocalDateTime.of(2026, 8, 15, 18, 30);

        UserResponse response =
                new UserResponse(
                        1L,
                        "Robert",
                        "Oprea",
                        "robert@example.com",
                        "0798765432",
                        createdate
                );

        when(
                userService.updateUser(
                        eq(1L),
                        any(UpdateUserRequest.class),
                        eq("mihai@example.com"),
                        eq(false)
                )
        ).thenReturn(response);

        mockMvc.perform(
                put("/users/1")
                        .with(
                                userJwt("mihai@example.com")
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstname": "Robert",
                                    "lastname": "Oprea",
                                    "email": "robert@example.com",
                                    "password": "newPassword",
                                    "phonenumber": "0798765432"
                                }
                                """)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.firstname")
                .value("Robert"))
        .andExpect(jsonPath("$.email")
                .value("robert@example.com"));
    }

    @Test
    void shouldReturn403WhenUserUpdatesAnotherUser()
            throws Exception {

        when(
                userService.updateUser(
                        eq(2L),
                        any(UpdateUserRequest.class),
                        eq("mihai@example.com"),
                        eq(false)
                )
        ).thenThrow(
                new org.springframework.security.access.AccessDeniedException(
                        "You do not have permission"
                )
        );

        mockMvc.perform(
                put("/users/2")
                        .with(
                                userJwt("mihai@example.com")
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstname": "Hacked",
                                    "lastname": "User",
                                    "email": "hacked@example.com",
                                    "password": "password123",
                                    "phonenumber": "0700000000"
                                }
                                """)
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToUpdateAnotherUser()
            throws Exception {

        UserResponse response =
                new UserResponse(
                        2L,
                        "Johnny",
                        "Doe",
                        "johnny@example.com",
                        "0799999999",
                        null
                );

        when(
                userService.updateUser(
                        eq(2L),
                        any(UpdateUserRequest.class),
                        eq("admin@example.com"),
                        eq(true)
                )
        ).thenReturn(response);

        mockMvc.perform(
                put("/users/2")
                        .with(
                                adminJwt("admin@example.com")
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstname": "Johnny",
                                    "lastname": "Doe",
                                    "email": "johnny@example.com",
                                    "password": "newPassword",
                                    "phonenumber": "0799999999"
                                }
                                """)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(2))
        .andExpect(jsonPath("$.firstname")
                .value("Johnny"))
        .andExpect(jsonPath("$.email")
                .value("johnny@example.com"));
    }

    // =========================================================
    // DELETE USER
    // =========================================================

    @Test
    void shouldReturn403WhenUserDeletesUser()
            throws Exception {

        mockMvc.perform(
                delete("/users/2")
                        .with(
                                userJwt("mihai@example.com")
                        )
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToDeleteUser()
            throws Exception {

        mockMvc.perform(
                delete("/users/2")
                        .with(
                                adminJwt("admin@example.com")
                        )
        )
        .andExpect(status().isNoContent());
    }
}