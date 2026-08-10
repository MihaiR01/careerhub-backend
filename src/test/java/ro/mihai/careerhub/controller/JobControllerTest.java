package ro.mihai.careerhub.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.http.MediaType;

import ro.mihai.careerhub.dto.request.CreateJobRequest;
import ro.mihai.careerhub.dto.response.JobResponse;
import ro.mihai.careerhub.enums.EmploymentType;
import ro.mihai.careerhub.exception.JobNotFoundException;
import ro.mihai.careerhub.service.JobService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest(JobController.class)
@AutoConfigureMockMvc(addFilters = false)
class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JobService jobService;

    @Test
    void shouldCreateJob() throws Exception {

        LocalDateTime createdate =
                LocalDateTime.of(2026, 8, 11, 12, 0);

        JobResponse response = new JobResponse(
                1L,
                "Java Backend Developer",
                "Java, Spring Boot, PostgreSQL",
                "Bucharest",
                EmploymentType.FULL_TIME,
                createdate,
                1L
        );

        when(jobService.createJob(any(CreateJobRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                post("/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Java Backend Developer",
                                    "technologies": "Java, Spring Boot, PostgreSQL",
                                    "location": "Bucharest",
                                    "employmentType": "FULL_TIME",
                                    "companyId": 1
                                }
                                """)
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title")
                .value("Java Backend Developer"))
        .andExpect(jsonPath("$.technologies")
                .value("Java, Spring Boot, PostgreSQL"))
        .andExpect(jsonPath("$.location")
                .value("Bucharest"))
        .andExpect(jsonPath("$.employmentType")
                .value("FULL_TIME"))
        .andExpect(jsonPath("$.companyId")
                .value(1))
        .andExpect(jsonPath("$.createdate")
                .value("2026-08-11T12:00:00"));
    }

    @Test
    void shouldReturn400WhenTitleIsMissing() throws Exception {

        mockMvc.perform(
                post("/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "technologies": "Java, Spring Boot",
                                    "location": "Cluj-Napoca",
                                    "employmentType": "FULL_TIME",
                                    "companyId": 1
                                }
                                """)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenTechnologiesAreMissing() throws Exception {

        mockMvc.perform(
                post("/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Java Backend Developer",
                                    "location": "Cluj-Napoca",
                                    "employmentType": "FULL_TIME",
                                    "companyId": 1
                                }
                                """)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenLocationIsMissing() throws Exception {

        mockMvc.perform(
                post("/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Java Backend Developer",
                                    "technologies": "Java, Spring Boot",
                                    "employmentType": "FULL_TIME",
                                    "companyId": 1
                                }
                                """)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenEmploymentTypeIsMissing() throws Exception {

        mockMvc.perform(
                post("/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Java Backend Developer",
                                    "technologies": "Java, Spring Boot",
                                    "location": "Cluj-Napoca",
                                    "companyId": 1
                                }
                                """)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenCompanyIdIsMissing() throws Exception {

        mockMvc.perform(
                post("/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Java Backend Developer",
                                    "technologies": "Java, Spring Boot, PostgreSQL",
                                    "location": "Bucharest",
                                    "employmentType": "FULL_TIME"
                                }
                                """)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetAllJobs() throws Exception {

        JobResponse response1 = new JobResponse(
                1L,
                "Java Backend Developer",
                "Java, Spring Boot, PostgreSQL, Docker",
                "Cluj-Napoca",
                EmploymentType.FULL_TIME,
                null,
                1L
        );

        JobResponse response2 = new JobResponse(
                2L,
                "C++ Developer",
                "C++, Linux, Git",
                "Bucharest",
                EmploymentType.FULL_TIME,
                null,
                1L
        );

        when(jobService.getAllJobs())
                .thenReturn(List.of(response1, response2));

        mockMvc.perform(
                get("/jobs")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))

        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].title")
                .value("Java Backend Developer"))
        .andExpect(jsonPath("$[0].technologies")
                .value("Java, Spring Boot, PostgreSQL, Docker"))
        .andExpect(jsonPath("$[0].location")
                .value("Cluj-Napoca"))
        .andExpect(jsonPath("$[0].employmentType")
                .value("FULL_TIME"))
        .andExpect(jsonPath("$[0].companyId").value(1))

        .andExpect(jsonPath("$[1].id").value(2))
        .andExpect(jsonPath("$[1].title")
                .value("C++ Developer"))
        .andExpect(jsonPath("$[1].technologies")
                .value("C++, Linux, Git"))
        .andExpect(jsonPath("$[1].location")
                .value("Bucharest"))
        .andExpect(jsonPath("$[1].employmentType")
                .value("FULL_TIME"))
        .andExpect(jsonPath("$[1].companyId").value(1));
    }

    @Test
    void shouldGetJobById() throws Exception {

        JobResponse response = new JobResponse(
                1L,
                "Java Backend Developer",
                "Java, Spring Boot, PostgreSQL, Docker",
                "Cluj-Napoca",
                EmploymentType.FULL_TIME,
                null,
                1L
        );

        when(jobService.getJobById(1L))
                .thenReturn(response);

        mockMvc.perform(
                get("/jobs/1")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title")
                .value("Java Backend Developer"))
        .andExpect(jsonPath("$.technologies")
                .value("Java, Spring Boot, PostgreSQL, Docker"))
        .andExpect(jsonPath("$.location")
                .value("Cluj-Napoca"))
        .andExpect(jsonPath("$.employmentType")
                .value("FULL_TIME"))
        .andExpect(jsonPath("$.companyId").value(1));
    }

    @Test
    void shouldReturn404WhenJobDoesNotExist() throws Exception {

        when(jobService.getJobById(999L))
                .thenThrow(new JobNotFoundException(999L));

        mockMvc.perform(
                get("/jobs/999")
        )
        .andExpect(status().isNotFound());
    }
}