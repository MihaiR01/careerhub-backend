package ro.mihai.careerhub.controller;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ro.mihai.careerhub.config.JwtConfig;
import ro.mihai.careerhub.config.PasswordConfig;
import ro.mihai.careerhub.config.SecurityConfig;
import ro.mihai.careerhub.dto.request.CreateJobApplicationRequest;
import ro.mihai.careerhub.dto.request.UpdateApplicationStatusRequest;
import ro.mihai.careerhub.dto.response.JobApplicationResponse;
import ro.mihai.careerhub.enums.ApplicationStatus;
import ro.mihai.careerhub.exception.DuplicateJobApplicationException;
import ro.mihai.careerhub.exception.InvalidApplicationStatusTransitionException;
import ro.mihai.careerhub.exception.JobApplicationNotFoundException;
import ro.mihai.careerhub.exception.JobNotFoundException;
import ro.mihai.careerhub.exception.UserNotFoundException;
import ro.mihai.careerhub.service.CustomUserDetailsService;
import ro.mihai.careerhub.service.JobApplicationService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JobApplicationController.class)
@Import({
        SecurityConfig.class,
        PasswordConfig.class,
        JwtConfig.class
})
class JobApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JobApplicationService jobApplicationService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldCreateApplication() throws Exception {

        JobApplicationResponse response =
                new JobApplicationResponse(
                        1L,
                        1L,
                        5L,
                        ApplicationStatus.APPLIED,
                        null
                );

        when(
                jobApplicationService.createApplication(
                        eq("mihai@example.com"),
                        any(CreateJobApplicationRequest.class)
                )
        ).thenReturn(response);

        mockMvc.perform(
                post("/applications")
                        .with(
                                user("mihai@example.com")
                                        .roles("USER")
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "jobId": 5
                                }
                                """)
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.userId").value(1))
        .andExpect(jsonPath("$.jobId").value(5))
        .andExpect(jsonPath("$.status")
                .value("APPLIED"));
    }

    @Test
    void shouldReturn400WhenJobIdIsMissing()
            throws Exception {

        mockMvc.perform(
                post("/applications")
                        .with(
                                user("mihai@example.com")
                                        .roles("USER")
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                }
                                """)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn409WhenApplicationAlreadyExists()
            throws Exception {

        when(
                jobApplicationService.createApplication(
                        eq("mihai@example.com"),
                        any(CreateJobApplicationRequest.class)
                )
        ).thenThrow(
                new DuplicateJobApplicationException(
                        1L,
                        5L
                )
        );

        mockMvc.perform(
                post("/applications")
                        .with(
                                user("mihai@example.com")
                                        .roles("USER")
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "jobId": 5
                                }
                                """)
        )
        .andExpect(status().isConflict());
    }

    @Test
    void shouldGetAllApplications() throws Exception {

        JobApplicationResponse response1 =
                new JobApplicationResponse(
                        1L,
                        1L,
                        5L,
                        ApplicationStatus.APPLIED,
                        null
                );

        JobApplicationResponse response2 =
                new JobApplicationResponse(
                        2L,
                        2L,
                        6L,
                        ApplicationStatus.UNDER_REVIEW,
                        null
                );

        when(jobApplicationService.getAllApplications())
                .thenReturn(List.of(
                        response1,
                        response2
                ));

        mockMvc.perform(
                get("/applications")
                        .with(
                                user("mihai@example.com")
                                        .roles("USER")
                        )
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].userId").value(1))
        .andExpect(jsonPath("$[0].jobId").value(5))
        .andExpect(jsonPath("$[0].status")
                .value("APPLIED"))
        .andExpect(jsonPath("$[1].id").value(2))
        .andExpect(jsonPath("$[1].userId").value(2))
        .andExpect(jsonPath("$[1].jobId").value(6))
        .andExpect(jsonPath("$[1].status")
                .value("UNDER_REVIEW"));
    }

    @Test
    void shouldGetApplicationById() throws Exception {

        JobApplicationResponse response =
                new JobApplicationResponse(
                        1L,
                        1L,
                        5L,
                        ApplicationStatus.APPLIED,
                        null
                );

        when(
                jobApplicationService.getApplicationById(1L)
        ).thenReturn(response);

        mockMvc.perform(
                get("/applications/1")
                        .with(
                                user("mihai@example.com")
                                        .roles("USER")
                        )
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.userId").value(1))
        .andExpect(jsonPath("$.jobId").value(5))
        .andExpect(jsonPath("$.status")
                .value("APPLIED"));
    }

    @Test
    void shouldReturn404WhenApplicationDoesNotExist()
            throws Exception {

        when(
                jobApplicationService.getApplicationById(999L)
        ).thenThrow(
                new JobApplicationNotFoundException(999L)
        );

        mockMvc.perform(
                get("/applications/999")
                        .with(
                                user("mihai@example.com")
                                        .roles("USER")
                        )
        )
        .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateApplicationStatus()
            throws Exception {

        JobApplicationResponse response =
                new JobApplicationResponse(
                        1L,
                        1L,
                        5L,
                        ApplicationStatus.UNDER_REVIEW,
                        null
                );

        when(
                jobApplicationService.updateApplicationStatus(
                        any(Long.class),
                        any(UpdateApplicationStatusRequest.class)
                )
        ).thenReturn(response);

        mockMvc.perform(
                put("/applications/1/status")
                        .with(
                                user("admin@example.com")
                                        .roles("ADMIN")
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "status": "UNDER_REVIEW"
                                }
                                """)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.userId").value(1))
        .andExpect(jsonPath("$.jobId").value(5))
        .andExpect(jsonPath("$.status")
                .value("UNDER_REVIEW"));
    }

    @Test
    void shouldReturn400WhenApplicationStatusIsMissing()
            throws Exception {

        mockMvc.perform(
                put("/applications/1/status")
                        .with(
                                user("admin@example.com")
                                        .roles("ADMIN")
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                }
                                """)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenStatusTransitionIsInvalid()
            throws Exception {

        when(
                jobApplicationService.updateApplicationStatus(
                        any(Long.class),
                        any(UpdateApplicationStatusRequest.class)
                )
        ).thenThrow(
                new InvalidApplicationStatusTransitionException(
                        ApplicationStatus.APPLIED,
                        ApplicationStatus.ACCEPTED
                )
        );

        mockMvc.perform(
                put("/applications/1/status")
                        .with(
                                user("admin@example.com")
                                        .roles("ADMIN")
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "status": "ACCEPTED"
                                }
                                """)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingApplication()
            throws Exception {

        when(
                jobApplicationService.updateApplicationStatus(
                        any(Long.class),
                        any(UpdateApplicationStatusRequest.class)
                )
        ).thenThrow(
                new JobApplicationNotFoundException(999L)
        );

        mockMvc.perform(
                put("/applications/999/status")
                        .with(
                                user("admin@example.com")
                                        .roles("ADMIN")
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "status": "UNDER_REVIEW"
                                }
                                """)
        )
        .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetApplicationsByUserId()
            throws Exception {

        JobApplicationResponse response1 =
                new JobApplicationResponse(
                        10L,
                        1L,
                        5L,
                        ApplicationStatus.APPLIED,
                        null
                );

        JobApplicationResponse response2 =
                new JobApplicationResponse(
                        11L,
                        1L,
                        6L,
                        ApplicationStatus.UNDER_REVIEW,
                        null
                );

        when(
                jobApplicationService.getApplicationsByUserId(1L)
        ).thenReturn(List.of(
                response1,
                response2
        ));

        mockMvc.perform(
                get("/applications/user/1")
                        .with(
                                user("mihai@example.com")
                                        .roles("USER")
                        )
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].id").value(10))
        .andExpect(jsonPath("$[0].userId").value(1))
        .andExpect(jsonPath("$[0].jobId").value(5))
        .andExpect(jsonPath("$[0].status")
                .value("APPLIED"))
        .andExpect(jsonPath("$[1].id").value(11))
        .andExpect(jsonPath("$[1].userId").value(1))
        .andExpect(jsonPath("$[1].jobId").value(6))
        .andExpect(jsonPath("$[1].status")
                .value("UNDER_REVIEW"));
    }

    @Test
    void shouldReturnEmptyApplicationsForUser()
            throws Exception {

        when(
                jobApplicationService.getApplicationsByUserId(1L)
        ).thenReturn(List.of());

        mockMvc.perform(
                get("/applications/user/1")
                        .with(
                                user("mihai@example.com")
                                        .roles("USER")
                        )
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturn404WhenUserDoesNotExistForApplications()
            throws Exception {

        when(
                jobApplicationService.getApplicationsByUserId(999L)
        ).thenThrow(
                new UserNotFoundException(999L)
        );

        mockMvc.perform(
                get("/applications/user/999")
                        .with(
                                user("mihai@example.com")
                                        .roles("USER")
                        )
        )
        .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetApplicationsByJobId()
            throws Exception {

        JobApplicationResponse response1 =
                new JobApplicationResponse(
                        10L,
                        1L,
                        5L,
                        ApplicationStatus.APPLIED,
                        null
                );

        JobApplicationResponse response2 =
                new JobApplicationResponse(
                        11L,
                        2L,
                        5L,
                        ApplicationStatus.ACCEPTED,
                        null
                );

        when(
                jobApplicationService.getApplicationsByJobId(5L)
        ).thenReturn(List.of(
                response1,
                response2
        ));

        mockMvc.perform(
                get("/applications/job/5")
                        .with(
                                user("mihai@example.com")
                                        .roles("USER")
                        )
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].id").value(10))
        .andExpect(jsonPath("$[0].userId").value(1))
        .andExpect(jsonPath("$[0].jobId").value(5))
        .andExpect(jsonPath("$[0].status")
                .value("APPLIED"))
        .andExpect(jsonPath("$[1].id").value(11))
        .andExpect(jsonPath("$[1].userId").value(2))
        .andExpect(jsonPath("$[1].jobId").value(5))
        .andExpect(jsonPath("$[1].status")
                .value("ACCEPTED"));
    }

    @Test
    void shouldReturnEmptyApplicationsForJob()
            throws Exception {

        when(
                jobApplicationService.getApplicationsByJobId(5L)
        ).thenReturn(List.of());

        mockMvc.perform(
                get("/applications/job/5")
                        .with(
                                user("mihai@example.com")
                                        .roles("USER")
                        )
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturn404WhenJobDoesNotExistForApplications()
            throws Exception {

        when(
                jobApplicationService.getApplicationsByJobId(999L)
        ).thenThrow(
                new JobNotFoundException(999L)
        );

        mockMvc.perform(
                get("/applications/job/999")
                        .with(
                                user("mihai@example.com")
                                        .roles("USER")
                        )
        )
        .andExpect(status().isNotFound());
    }
}