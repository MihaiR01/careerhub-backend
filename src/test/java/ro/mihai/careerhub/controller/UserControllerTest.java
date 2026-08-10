package ro.mihai.careerhub.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ro.mihai.careerhub.dto.response.UserResponse;
import ro.mihai.careerhub.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.MediaType;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void shouldCreateUser() throws Exception {

        LocalDateTime createdate =
                LocalDateTime.of(2026, 8, 10, 20, 30);

        UserResponse response = new UserResponse(
                1L,
                "Mihai",
                "Oprea",
                "mihai@example.com",
                "0712345678",
                createdate
        );

        when(userService.createUser(any()))
                .thenReturn(response);

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
        .andExpect(jsonPath("$.firstname").value("Mihai"))
        .andExpect(jsonPath("$.lastname").value("Oprea"))
        .andExpect(jsonPath("$.email").value("mihai@example.com"))
        .andExpect(jsonPath("$.phonenumber").value("0712345678"))
        .andExpect(jsonPath("$.createdate")
                .value("2026-08-10T20:30:00"));
    }

    @Test
    void shouldReturn400WhenFirstnameIsMissing() throws Exception {

        mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "lastname": "Oprea",
                                    "email": "mihai@example.com",
                                    "password": "password123",
                                    "phonenumber": "0712345678"
                                }
                                """)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenLastnameIsMissing() throws Exception {

        mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstname": "Mihai",
                                    "email": "mihai@example.com",
                                    "password": "password123",
                                    "phonenumber": "0712345678"
                                }
                                """)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenEmailIsMissing() throws Exception {

        mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstname": "Mihai",
                                    "lastname": "Oprea",
                                    "password": "password123",
                                    "phonenumber": "0712345678"
                                }
                                """)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenEmailIsInvalid() throws Exception {

        mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstname": "Mihai",
                                    "lastname": "Oprea",
                                    "email": "invalid-email",
                                    "password": "password123",
                                    "phonenumber": "0712345678"
                                }
                                """)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenPasswordIsMissing() throws Exception {

        mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstname": "Mihai",
                                    "lastname": "Oprea",
                                    "email": "mihai@example.com",
                                    "phonenumber": "0712345678"
                                }
                                """)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenPhoneNumberIsMissing() throws Exception {

        mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstname": "Mihai",
                                    "lastname": "Oprea",
                                    "email": "mihai@example.com",
                                    "password": "password123"
                                }
                                """)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetAllUsers() throws Exception {

        LocalDateTime createdate1 =
                LocalDateTime.of(2026, 8, 10, 20, 30);

        LocalDateTime createdate2 =
                LocalDateTime.of(2026, 8, 11, 10, 15);

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

        when(userService.getAllUsers())
                .thenReturn(List.of(response1, response2));

        mockMvc.perform(
                get("/users")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))

        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].firstname").value("Mihai"))
        .andExpect(jsonPath("$[0].lastname").value("Oprea"))
        .andExpect(jsonPath("$[0].email")
                .value("mihai.one@example.com"))
        .andExpect(jsonPath("$[0].phonenumber")
                .value("0712345678"))
        .andExpect(jsonPath("$[0].createdate")
                .value("2026-08-10T20:30:00"))

        .andExpect(jsonPath("$[1].id").value(2))
        .andExpect(jsonPath("$[1].firstname").value("John"))
        .andExpect(jsonPath("$[1].lastname").value("Doe"))
        .andExpect(jsonPath("$[1].email")
                .value("john.doe@example.com"))
        .andExpect(jsonPath("$[1].phonenumber")
                .value("0723456789"))
        .andExpect(jsonPath("$[1].createdate")
                .value("2026-08-11T10:15:00"));
    }

    @Test
    void shouldReturnEmptyUserList() throws Exception {

        when(userService.getAllUsers())
                .thenReturn(List.of());

        mockMvc.perform(
                get("/users")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldGetUserById() throws Exception {

        LocalDateTime createdate =
                LocalDateTime.of(2026, 8, 11, 10, 30);

        UserResponse response = new UserResponse(
                1L,
                "Mihai",
                "Oprea",
                "mihai.get@example.com",
                "0712345678",
                createdate
        );

        when(userService.getUserById(1L))
                .thenReturn(response);

        mockMvc.perform(
                get("/users/1")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.firstname").value("Mihai"))
        .andExpect(jsonPath("$.lastname").value("Oprea"))
        .andExpect(jsonPath("$.email")
                .value("mihai.get@example.com"))
        .andExpect(jsonPath("$.phonenumber")
                .value("0712345678"))
        .andExpect(jsonPath("$.createdate")
                .value("2026-08-11T10:30:00"));
    }

    @Test
    void shouldDeleteUser() throws Exception {

        mockMvc.perform(
                delete("/users/1")
        )
        .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @Test
    void shouldUpdateUser() throws Exception {

        LocalDateTime createdate =
                LocalDateTime.of(2026, 8, 1, 12, 30);

        UserResponse response = new UserResponse(
                1L,
                "Robert",
                "Oprea",
                "robert@example.com",
                "0798765432",
                createdate
        );

        when(userService.updateUser(
                any(Long.class),
                any()
        )).thenReturn(response);

        mockMvc.perform(
                put("/users/1")
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
        .andExpect(jsonPath("$.firstname").value("Robert"))
        .andExpect(jsonPath("$.lastname").value("Oprea"))
        .andExpect(jsonPath("$.email")
                .value("robert@example.com"))
        .andExpect(jsonPath("$.phonenumber")
                .value("0798765432"))
        .andExpect(jsonPath("$.createdate")
                .value("2026-08-01T12:30:00"));
    }

    @Test
    void shouldReturn400WhenUpdatingUserWithInvalidEmail()
            throws Exception {

        mockMvc.perform(
                put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstname": "Robert",
                                    "lastname": "Oprea",
                                    "email": "invalid-email",
                                    "password": "newPassword",
                                    "phonenumber": "0798765432"
                                }
                                """)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenUpdatingUserWithMissingFirstname()
            throws Exception {

        mockMvc.perform(
                put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "lastname": "Oprea",
                                    "email": "robert@example.com",
                                    "password": "newPassword",
                                    "phonenumber": "0798765432"
                                }
                                """)
        )
        .andExpect(status().isBadRequest());
    }
}