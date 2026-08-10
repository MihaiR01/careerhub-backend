package ro.mihai.careerhub.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ro.mihai.careerhub.dto.response.CompanyResponse;
import ro.mihai.careerhub.exception.CompanyNotFoundException;
import ro.mihai.careerhub.service.CompanyService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.springframework.http.MediaType;

@WebMvcTest(CompanyController.class)
@AutoConfigureMockMvc(addFilters = false)
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
        post("/companies")
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
    void shouldReturn400WhenCompanyNameIsMissing() throws Exception {

    mockMvc.perform(
            post("/companies")
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
            post("/companies")
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
            post("/companies")
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

    @Test
    void shouldGetAllCompanies() throws Exception {

    CompanyResponse response1 = new CompanyResponse(
            1L,
            "Nexttech",
            "Cluj-Napoca",
            "https://www.nexttech.ro"
    );

    CompanyResponse response2 = new CompanyResponse(
            2L,
            "Google",
            "Bucharest",
            "https://google.com"
    );

    when(companyService.getAllCompanies())
            .thenReturn(List.of(response1, response2));

    mockMvc.perform(
            get("/companies")
    )
    .andExpect(status().isOk())
    .andExpect(jsonPath("$").isArray())
    .andExpect(jsonPath("$.length()").value(2))

    .andExpect(jsonPath("$[0].id").value(1))
    .andExpect(jsonPath("$[0].name")
            .value("Nexttech"))
    .andExpect(jsonPath("$[0].city")
            .value("Cluj-Napoca"))
    .andExpect(jsonPath("$[0].website")
            .value("https://www.nexttech.ro"))

    .andExpect(jsonPath("$[1].id").value(2))
    .andExpect(jsonPath("$[1].name")
            .value("Google"))
    .andExpect(jsonPath("$[1].city")
            .value("Bucharest"))
    .andExpect(jsonPath("$[1].website")
            .value("https://google.com"));
    }

    @Test
    void shouldGetCompanyById() throws Exception {

        CompanyResponse response = new CompanyResponse(
                1L,
                "Nexttech",
                "Cluj-Napoca",
                "https://www.nexttech.ro"
        );

        when(companyService.getCompanyById(1L))
                .thenReturn(response);

        mockMvc.perform(
                get("/companies/1")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Nexttech"))
        .andExpect(jsonPath("$.city").value("Cluj-Napoca"))
        .andExpect(jsonPath("$.website")
                .value("https://www.nexttech.ro"));
    }

    @Test
    void shouldReturn404WhenCompanyDoesNotExist() throws Exception {

        when(companyService.getCompanyById(999L))
                .thenThrow(new CompanyNotFoundException(999L));

        mockMvc.perform(
                get("/companies/999")
        )
        .andExpect(status().isNotFound());
    }
}