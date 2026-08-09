package ro.mihai.careerhub.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ro.mihai.careerhub.dto.response.CompanyResponse;
import ro.mihai.careerhub.service.CompanyService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import org.springframework.http.MediaType;

@WebMvcTest(CompanyController.class)
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompanyService companyService;

    @Test
    void shouldCreateCompany() throws Exception {

        CompanyResponse response = new CompanyResponse(
                1L,
                "Google",
                "Bucharest",
                "https://google.com"
        );

        when(companyService.createCompany(any()))
                .thenReturn(response);

        mockMvc.perform(
        post("/api/companies")
                .with(user("testuser"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "Google",
                            "city": "Bucharest",
                            "website": "https://google.com"
                        }
                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Google"))
                .andExpect(jsonPath("$.city").value("Bucharest"))
                .andExpect(jsonPath("$.website").value("https://google.com"));
    }

    @Test
    void shouldReturn401WhenUserIsNotAuthenticated() throws Exception {

        mockMvc.perform(
                post("/api/companies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Nexttech",
                                    "city": "Cluj-Napoca",
                                    "website": "https://nexttech.talentlyft.com/"
                                }
                                """)
        )
        .andExpect(status().isUnauthorized());
    }

        @Test
        void shouldReturn400WhenCompanyNameIsMissing() throws Exception {

        mockMvc.perform(
                post("/api/companies")
                        .with(user("testuser"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                        "city": "Cluj-Napoca",
                                        "website": "https://nexttech.talentlyft.com/"
                                }
                                """)
        )
        .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturn400WhenCompanyCityIsMissing() throws Exception {

        mockMvc.perform(
                post("/api/companies")
                        .with(user("testuser"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                        "name": "Nexttech",
                                        "website": "https://nexttech.talentlyft.com/"
                                }
                                """)
        )
        .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturn400WhenCompanyWebsiteIsMissing() throws Exception {

        mockMvc.perform(
                post("/api/companies")
                        .with(user("testuser"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                        "name": "Nexttech",
                                        "city": "Cluj-Napoca"
                                }
                                """)
        )
        .andExpect(status().isBadRequest());
        }
}