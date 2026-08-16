package ro.mihai.careerhub.security;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ro.mihai.careerhub.config.JwtConfig;
import ro.mihai.careerhub.config.PasswordConfig;
import ro.mihai.careerhub.config.SecurityConfig;
import ro.mihai.careerhub.controller.JobApplicationController;
import ro.mihai.careerhub.controller.JobController;
import ro.mihai.careerhub.controller.UserController;
import ro.mihai.careerhub.dto.response.JobApplicationResponse;
import ro.mihai.careerhub.dto.response.JobResponse;
import ro.mihai.careerhub.dto.response.UserResponse;
import ro.mihai.careerhub.enums.ApplicationStatus;
import ro.mihai.careerhub.enums.EmploymentType;
import ro.mihai.careerhub.service.CustomUserDetailsService;
import ro.mihai.careerhub.service.JobApplicationService;
import ro.mihai.careerhub.service.JobService;
import ro.mihai.careerhub.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({
        JobController.class,
        JobApplicationController.class,
        UserController.class
})
@Import({
        SecurityConfig.class,
        PasswordConfig.class,
        JwtConfig.class
})
class SecurityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JobService jobService;

    @MockitoBean
    private JobApplicationService jobApplicationService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    // =========================================================
    // PUBLIC JOB ENDPOINTS
    // =========================================================

    @Test
    void shouldAllowPublicGetJobsWithoutAuthentication()
            throws Exception {

        when(
                jobService.getJobs(
                        any(),
                        any(),
                        any(),
                        any()
                )
        )
        .thenReturn(
                org.springframework.data.domain.Page.empty()
        );

        mockMvc.perform(
                get("/jobs")
        )
        .andExpect(status().isOk());
    }

    @Test
    void shouldAllowPublicGetJobByIdWithoutAuthentication()
            throws Exception {

        JobResponse response =
                new JobResponse(
                        1L,
                        "Java Backend Developer",
                        "Java, Spring Boot",
                        "Cluj-Napoca",
                        EmploymentType.FULL_TIME,
                        null,
                        1L
                );

        when(
                jobService.getJobById(1L)
        )
        .thenReturn(response);

        mockMvc.perform(
                get("/jobs/1")
        )
        .andExpect(status().isOk());
    }

    // =========================================================
    // JOB SECURITY
    // =========================================================

    @Test
    void shouldReturn401WhenCreatingJobWithoutAuthentication()
            throws Exception {

        mockMvc.perform(
                post("/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Java Backend Developer",
                                    "technologies": "Java, Spring Boot",
                                    "location": "Cluj-Napoca",
                                    "employmentType": "FULL_TIME",
                                    "companyId": 1
                                }
                                """)
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(
            username = "user@example.com",
            roles = "USER"
    )
    void shouldReturn403WhenUserCreatesJob()
            throws Exception {

        mockMvc.perform(
                post("/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Java Backend Developer",
                                    "technologies": "Java, Spring Boot",
                                    "location": "Cluj-Napoca",
                                    "employmentType": "FULL_TIME",
                                    "companyId": 1
                                }
                                """)
        )
        .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(
            username = "admin@example.com",
            roles = "ADMIN"
    )
    void shouldAllowAdminToCreateJob()
            throws Exception {

        JobResponse response =
                new JobResponse(
                        1L,
                        "Java Backend Developer",
                        "Java, Spring Boot",
                        "Cluj-Napoca",
                        EmploymentType.FULL_TIME,
                        null,
                        1L
                );

        when(
                jobService.createJob(any())
        )
        .thenReturn(response);

        mockMvc.perform(
                post("/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Java Backend Developer",
                                    "technologies": "Java, Spring Boot",
                                    "location": "Cluj-Napoca",
                                    "employmentType": "FULL_TIME",
                                    "companyId": 1
                                }
                                """)
        )
        .andExpect(status().isCreated());
    }

    // =========================================================
    // APPLICATION SECURITY
    // =========================================================

    @Test
    void shouldReturn401WhenCreatingApplicationWithoutAuthentication()
            throws Exception {

        mockMvc.perform(
                post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "jobId": 5
                                }
                                """)
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(
            username = "user@example.com",
            roles = "USER"
    )
    void shouldAllowUserToCreateApplication()
            throws Exception {

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
                        any(String.class),
                        any()
                )
        )
        .thenReturn(response);

        mockMvc.perform(
                post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "jobId": 5
                                }
                                """)
        )
        .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(
            username = "admin@example.com",
            roles = "ADMIN"
    )
    void shouldReturn403WhenAdminCreatesApplication()
            throws Exception {

        mockMvc.perform(
                post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "jobId": 5
                                }
                                """)
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn401WhenGettingApplicationsWithoutAuthentication()
            throws Exception {

        mockMvc.perform(
                get("/applications")
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(
            username = "user@example.com",
            roles = "USER"
    )
    void shouldAllowAuthenticatedUserToGetApplications()
            throws Exception {

        when(
                jobApplicationService.getAllApplications()
        )
        .thenReturn(List.of());

        mockMvc.perform(
                get("/applications")
        )
        .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(
            username = "user@example.com",
            roles = "USER"
    )
    void shouldReturn403WhenUserUpdatesApplicationStatus()
            throws Exception {

        mockMvc.perform(
                put("/applications/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "status": "UNDER_REVIEW"
                                }
                                """)
        )
        .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(
            username = "admin@example.com",
            roles = "ADMIN"
    )
    void shouldAllowAdminToUpdateApplicationStatus()
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
                        any()
                )
        )
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
        .andExpect(status().isOk());
    }

    // =========================================================
    // USER SECURITY
    // =========================================================

    @Test
    void shouldAllowRegistrationWithoutAuthentication()
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
                userService.createUser(any())
        )
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
        .andExpect(status().isCreated());
    }

    @Test
    void shouldReturn401WhenGettingAllUsersWithoutAuthentication()
            throws Exception {

        mockMvc.perform(
                get("/users")
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(
            username = "user@example.com",
            roles = "USER"
    )
    void shouldReturn403WhenUserGetsAllUsers()
            throws Exception {

        mockMvc.perform(
                get("/users")
        )
        .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(
            username = "admin@example.com",
            roles = "ADMIN"
    )
    void shouldAllowAdminToGetAllUsers()
            throws Exception {

        when(
                userService.getAllUsers()
        )
        .thenReturn(List.of());

        mockMvc.perform(
                get("/users")
        )
        .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WhenGettingUserByIdWithoutAuthentication()
            throws Exception {

        mockMvc.perform(
                get("/users/1")
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(
            username = "user@example.com",
            roles = "USER"
    )
    void shouldAllowAuthenticatedUserToGetUserById()
            throws Exception {

        UserResponse response =
                new UserResponse(
                        1L,
                        "Mihai",
                        "Oprea",
                        "user@example.com",
                        "0712345678",
                        null
                );

        when(
                userService.getUserById(
                        1L,
                        "user@example.com",
                        false
                )
        )
        .thenReturn(response);

        mockMvc.perform(
                get("/users/1")
        )
        .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(
            username = "admin@example.com",
            roles = "ADMIN"
    )
    void shouldAllowAdminToGetAnyUser()
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
        )
        .thenReturn(response);

        mockMvc.perform(
                get("/users/2")
        )
        .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WhenUpdatingUserWithoutAuthentication()
            throws Exception {

        mockMvc.perform(
                put("/users/1")
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
        .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(
            username = "user@example.com",
            roles = "USER"
    )
    void shouldAllowUserToUpdateOwnUser()
            throws Exception {

        UserResponse response =
                new UserResponse(
                        1L,
                        "Mihai",
                        "Oprea",
                        "user@example.com",
                        "0712345678",
                        null
                );

        when(
                userService.updateUser(
                        any(Long.class),
                        any(),
                        eq("user@example.com"),
                        eq(false)
                )
        )
        .thenReturn(response);

        mockMvc.perform(
                put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstname": "Mihai",
                                    "lastname": "Oprea",
                                    "email": "user@example.com",
                                    "password": "password123",
                                    "phonenumber": "0712345678"
                                }
                                """)
        )
        .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(
            username = "admin@example.com",
            roles = "ADMIN"
    )
    void shouldAllowAdminToUpdateAnyUser()
            throws Exception {

        UserResponse response =
                new UserResponse(
                        2L,
                        "John",
                        "Doe",
                        "john.new@example.com",
                        "0799999999",
                        null
                );

        when(
                userService.updateUser(
                        any(Long.class),
                        any(),
                        eq("admin@example.com"),
                        eq(true)
                )
        )
        .thenReturn(response);

        mockMvc.perform(
                put("/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstname": "John",
                                    "lastname": "Doe",
                                    "email": "john.new@example.com",
                                    "password": "password123",
                                    "phonenumber": "0799999999"
                                }
                                """)
        )
        .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WhenDeletingUserWithoutAuthentication()
            throws Exception {

        mockMvc.perform(
                delete("/users/1")
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(
            username = "user@example.com",
            roles = "USER"
    )
    void shouldReturn403WhenUserDeletesUser()
            throws Exception {

        mockMvc.perform(
                delete("/users/2")
        )
        .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(
            username = "admin@example.com",
            roles = "ADMIN"
    )
    void shouldAllowAdminToDeleteUser()
            throws Exception {

        mockMvc.perform(
                delete("/users/2")
        )
        .andExpect(status().isNoContent());
    }
}