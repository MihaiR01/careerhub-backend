package ro.mihai.careerhub.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ro.mihai.careerhub.dto.response.UserResponse;
import ro.mihai.careerhub.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

        UserResponse response = new UserResponse(
                1L,
                "Mihai",
                "Oprea",
                "mihai@example.com",
                "0712345678"
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
        .andExpect(jsonPath("$.phonenumber").value("0712345678"));
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
}