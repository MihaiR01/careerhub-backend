package ro.mihai.careerhub.controller;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ro.mihai.careerhub.dto.request.CreateJobRequest;
import ro.mihai.careerhub.dto.response.JobResponse;
import ro.mihai.careerhub.enums.EmploymentType;
import ro.mihai.careerhub.exception.JobNotFoundException;
import ro.mihai.careerhub.service.JobService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JobController.class)
@AutoConfigureMockMvc(addFilters = false)
class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JobService jobService;

    @Test
    void shouldCreateJob() throws Exception {

        JobResponse response = new JobResponse(
                1L,
                "Java Backend Developer",
                "Java, Spring Boot, PostgreSQL",
                "Bucharest",
                EmploymentType.FULL_TIME,
                null,
                1L
        );

        when(
                jobService.createJob(
                        any(CreateJobRequest.class)
                )
        )
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
        .andExpect(jsonPath("$.companyId").value(1));
    }

    @Test
    void shouldReturn400WhenTitleIsMissing()
            throws Exception {

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
    void shouldReturn400WhenTechnologiesAreMissing()
            throws Exception {

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
    void shouldReturn400WhenLocationIsMissing()
            throws Exception {

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
    void shouldReturn400WhenEmploymentTypeIsMissing()
            throws Exception {

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
    void shouldReturn400WhenCompanyIdIsMissing()
            throws Exception {

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

        Pageable pageable = PageRequest.of(
                0,
                20
        );

        Page<JobResponse> page = new PageImpl<>(
                List.of(response1, response2),
                pageable,
                2
        );

        when(
                jobService.getJobs(
                        isNull(),
                        isNull(),
                        isNull(),
                        any(Pageable.class)
                )
        )
                .thenReturn(page);

        mockMvc.perform(
                get("/jobs")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[0].id").value(1))
        .andExpect(jsonPath("$.content[0].title")
                .value("Java Backend Developer"))
        .andExpect(jsonPath("$.content[0].technologies")
                .value("Java, Spring Boot, PostgreSQL, Docker"))
        .andExpect(jsonPath("$.content[0].location")
                .value("Cluj-Napoca"))
        .andExpect(jsonPath("$.content[0].employmentType")
                .value("FULL_TIME"))
        .andExpect(jsonPath("$.content[0].companyId").value(1))
        .andExpect(jsonPath("$.content[1].id").value(2))
        .andExpect(jsonPath("$.content[1].title")
                .value("C++ Developer"))
        .andExpect(jsonPath("$.number").value(0))
        .andExpect(jsonPath("$.size").value(20))
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void shouldFilterJobsByLocation()
            throws Exception {

        JobResponse response = new JobResponse(
                1L,
                "Java Backend Developer",
                "Java, Spring Boot",
                "Cluj-Napoca",
                EmploymentType.FULL_TIME,
                null,
                1L
        );

        Page<JobResponse> page =
                new PageImpl<>(List.of(response));

        when(
                jobService.getJobs(
                        eq("Cluj-Napoca"),
                        isNull(),
                        isNull(),
                        any(Pageable.class)
                )
        )
                .thenReturn(page);

        mockMvc.perform(
                get("/jobs")
                        .param("location", "Cluj-Napoca")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].location")
                .value("Cluj-Napoca"));
    }

    @Test
    void shouldFilterJobsByEmploymentType()
            throws Exception {

        JobResponse response = new JobResponse(
                1L,
                "Java Backend Developer",
                "Java, Spring Boot",
                "Bucharest",
                EmploymentType.FULL_TIME,
                null,
                1L
        );

        Page<JobResponse> page =
                new PageImpl<>(List.of(response));

        when(
                jobService.getJobs(
                        isNull(),
                        eq(EmploymentType.FULL_TIME),
                        isNull(),
                        any(Pageable.class)
                )
        )
                .thenReturn(page);

        mockMvc.perform(
                get("/jobs")
                        .param(
                                "employmentType",
                                "FULL_TIME"
                        )
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].employmentType")
                .value("FULL_TIME"));
    }

    @Test
    void shouldFilterJobsByTechnology()
            throws Exception {

        JobResponse response = new JobResponse(
                1L,
                "Java Backend Developer",
                "Java, Spring Boot, PostgreSQL",
                "Bucharest",
                EmploymentType.FULL_TIME,
                null,
                1L
        );

        Page<JobResponse> page =
                new PageImpl<>(List.of(response));

        when(
                jobService.getJobs(
                        isNull(),
                        isNull(),
                        eq("Java"),
                        any(Pageable.class)
                )
        )
                .thenReturn(page);

        mockMvc.perform(
                get("/jobs")
                        .param("technologies", "Java")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].technologies")
                .value("Java, Spring Boot, PostgreSQL"));
    }

    @Test
    void shouldFilterAndPaginateJobs()
            throws Exception {

        JobResponse response = new JobResponse(
                1L,
                "Java Backend Developer",
                "Java, Spring Boot, PostgreSQL",
                "Cluj-Napoca",
                EmploymentType.FULL_TIME,
                null,
                1L
        );

        Page<JobResponse> page =
                new PageImpl<>(
                        List.of(response),
                        PageRequest.of(1, 5),
                        6
                );

        when(
                jobService.getJobs(
                        eq("Cluj-Napoca"),
                        eq(EmploymentType.FULL_TIME),
                        eq("Java"),
                        any(Pageable.class)
                )
        )
                .thenReturn(page);

        mockMvc.perform(
                get("/jobs")
                        .param("location", "Cluj-Napoca")
                        .param("employmentType", "FULL_TIME")
                        .param("technologies", "Java")
                        .param("page", "1")
                        .param("size", "5")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.number").value(1))
        .andExpect(jsonPath("$.size").value(5))
        .andExpect(jsonPath("$.totalElements").value(6))
        .andExpect(jsonPath("$.totalPages").value(2));
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
    void shouldReturn404WhenJobDoesNotExist()
            throws Exception {

        when(jobService.getJobById(999L))
                .thenThrow(
                        new JobNotFoundException(999L)
                );

        mockMvc.perform(
                get("/jobs/999")
        )
        .andExpect(status().isNotFound());
    }
}