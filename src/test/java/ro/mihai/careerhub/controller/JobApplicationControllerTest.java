package ro.mihai.careerhub.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ro.mihai.careerhub.dto.request.CreateJobApplicationRequest;
import ro.mihai.careerhub.dto.request.UpdateApplicationStatusRequest;
import ro.mihai.careerhub.dto.response.JobApplicationResponse;
import ro.mihai.careerhub.enums.ApplicationStatus;
import ro.mihai.careerhub.exception.InvalidApplicationStatusTransitionException;
import ro.mihai.careerhub.exception.JobApplicationNotFoundException;
import ro.mihai.careerhub.service.JobApplicationService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

@WebMvcTest(JobApplicationController.class)
@AutoConfigureMockMvc(addFilters = false)
class JobApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JobApplicationService jobApplicationService;

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

        when(jobApplicationService.createApplication(
                any(CreateJobApplicationRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "userId": 1,
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
    void shouldReturn400WhenUserIdIsMissing() throws Exception {

        mockMvc.perform(
                post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "jobId": 5
                                }
                                """)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenJobIdIsMissing() throws Exception {

        mockMvc.perform(
                post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "userId": 1
                                }
                                """)
        )
        .andExpect(status().isBadRequest());
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
                .thenReturn(List.of(response1, response2));

        mockMvc.perform(
                get("/applications")
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

        when(jobApplicationService.getApplicationById(1L))
                .thenReturn(response);

        mockMvc.perform(
                get("/applications/1")
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

        when(jobApplicationService.getApplicationById(999L))
                .thenThrow(
                        new JobApplicationNotFoundException(999L)
                );

        mockMvc.perform(
                get("/applications/999")
        )
        .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateApplicationStatus() throws Exception {

        JobApplicationResponse response =
                new JobApplicationResponse(
                        1L,
                        1L,
                        5L,
                        ApplicationStatus.UNDER_REVIEW,
                        null
                );

        when(jobApplicationService.updateApplicationStatus(
                any(Long.class),
                any(UpdateApplicationStatusRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                put("/applications/1/status")
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

        when(jobApplicationService.updateApplicationStatus(
                any(Long.class),
                any(UpdateApplicationStatusRequest.class)))
                .thenThrow(
                        new InvalidApplicationStatusTransitionException(
                                ApplicationStatus.APPLIED,
                                ApplicationStatus.ACCEPTED
                        )
                );

        mockMvc.perform(
                put("/applications/1/status")
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

        when(jobApplicationService.updateApplicationStatus(
                any(Long.class),
                any(UpdateApplicationStatusRequest.class)))
                .thenThrow(
                        new JobApplicationNotFoundException(999L)
                );

        mockMvc.perform(
                put("/applications/999/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "status": "UNDER_REVIEW"
                                }
                                """)
        )
        .andExpect(status().isNotFound());
    }
}