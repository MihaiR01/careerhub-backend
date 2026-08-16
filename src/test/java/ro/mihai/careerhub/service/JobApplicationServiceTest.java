package ro.mihai.careerhub.service;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ro.mihai.careerhub.dto.request.CreateJobApplicationRequest;
import ro.mihai.careerhub.dto.request.UpdateApplicationStatusRequest;
import ro.mihai.careerhub.dto.response.JobApplicationResponse;
import ro.mihai.careerhub.entity.Company;
import ro.mihai.careerhub.entity.Job;
import ro.mihai.careerhub.entity.JobApplication;
import ro.mihai.careerhub.entity.User;
import ro.mihai.careerhub.enums.ApplicationStatus;
import ro.mihai.careerhub.enums.EmploymentType;
import ro.mihai.careerhub.exception.DuplicateJobApplicationException;
import ro.mihai.careerhub.exception.InvalidApplicationStatusTransitionException;
import ro.mihai.careerhub.exception.JobApplicationNotFoundException;
import ro.mihai.careerhub.exception.JobNotFoundException;
import ro.mihai.careerhub.exception.UserNotFoundException;
import ro.mihai.careerhub.mapper.JobApplicationMapper;
import ro.mihai.careerhub.repository.JobApplicationRepository;
import ro.mihai.careerhub.repository.JobRepository;
import ro.mihai.careerhub.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JobApplicationServiceTest {

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private JobApplicationMapper jobApplicationMapper;

    private JobApplicationService jobApplicationService;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        jobApplicationService = new JobApplicationService(
                jobApplicationRepository,
                userRepository,
                jobRepository,
                jobApplicationMapper
        );
    }

    @Test
    void shouldCreateJobApplication() {

        String userEmail = "application@example.com";

        CreateJobApplicationRequest request =
                new CreateJobApplicationRequest(5L);

        User user = new User(
                "Mihai",
                "Oprea",
                userEmail,
                "encoded-password",
                "0712345678"
        );

        user.setId(1L);

        Company company = new Company(
                "Google",
                "Bucharest",
                "https://google.com"
        );

        company.setId(1L);

        Job job = new Job(
                "Java Backend Developer",
                "Java, Spring Boot, PostgreSQL",
                "Bucharest",
                EmploymentType.FULL_TIME,
                company
        );

        job.setId(5L);

        JobApplication application = new JobApplication(
                user,
                job,
                ApplicationStatus.APPLIED
        );

        application.setId(10L);

        JobApplicationResponse response =
                new JobApplicationResponse(
                        10L,
                        1L,
                        5L,
                        ApplicationStatus.APPLIED,
                        application.getCreatedate()
                );

        when(userRepository.findByEmail(userEmail))
                .thenReturn(Optional.of(user));

        when(jobRepository.findById(5L))
                .thenReturn(Optional.of(job));

        when(
                jobApplicationRepository.existsByUserIdAndJobId(
                        1L,
                        5L
                )
        )
                .thenReturn(false);

        when(
                jobApplicationRepository.save(
                        any(JobApplication.class)
                )
        )
                .thenReturn(application);

        when(jobApplicationMapper.toResponse(application))
                .thenReturn(response);

        JobApplicationResponse result =
                jobApplicationService.createApplication(
                        userEmail,
                        request
                );

        assertEquals(10L, result.getId());
        assertEquals(1L, result.getUserId());
        assertEquals(5L, result.getJobId());

        assertEquals(
                ApplicationStatus.APPLIED,
                result.getStatus()
        );

        assertNotNull(result.getCreatedate());

        verify(userRepository)
                .findByEmail(userEmail);

        verify(jobRepository)
                .findById(5L);

        verify(
                jobApplicationRepository
        ).existsByUserIdAndJobId(1L, 5L);

        verify(
                jobApplicationRepository
        ).save(any(JobApplication.class));

        verify(jobApplicationMapper)
                .toResponse(application);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {

        String userEmail = "missing@example.com";

        CreateJobApplicationRequest request =
                new CreateJobApplicationRequest(5L);

        when(userRepository.findByEmail(userEmail))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> jobApplicationService.createApplication(
                        userEmail,
                        request
                )
        );

        verify(userRepository)
                .findByEmail(userEmail);

        verifyNoInteractions(jobRepository);
        verifyNoInteractions(jobApplicationRepository);
        verifyNoInteractions(jobApplicationMapper);
    }

    @Test
    void shouldThrowJobNotFoundExceptionWhenJobDoesNotExist() {

        String userEmail = "jobnotfound@example.com";

        CreateJobApplicationRequest request =
                new CreateJobApplicationRequest(999L);

        User user = new User(
                "Mihai",
                "Oprea",
                userEmail,
                "encoded-password",
                "0712345678"
        );

        user.setId(1L);

        when(userRepository.findByEmail(userEmail))
                .thenReturn(Optional.of(user));

        when(jobRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                JobNotFoundException.class,
                () -> jobApplicationService.createApplication(
                        userEmail,
                        request
                )
        );

        verify(userRepository)
                .findByEmail(userEmail);

        verify(jobRepository)
                .findById(999L);

        verifyNoInteractions(jobApplicationRepository);
        verifyNoInteractions(jobApplicationMapper);
    }

    @Test
    void shouldThrowDuplicateJobApplicationException() {

        String userEmail = "duplicate@example.com";

        CreateJobApplicationRequest request =
                new CreateJobApplicationRequest(5L);

        User user = new User(
                "Mihai",
                "Oprea",
                userEmail,
                "encoded-password",
                "0712345678"
        );

        user.setId(1L);

        Company company = new Company(
                "Google",
                "Bucharest",
                "https://google.com"
        );

        company.setId(1L);

        Job job = new Job(
                "Java Backend Developer",
                "Java, Spring Boot",
                "Bucharest",
                EmploymentType.FULL_TIME,
                company
        );

        job.setId(5L);

        when(userRepository.findByEmail(userEmail))
                .thenReturn(Optional.of(user));

        when(jobRepository.findById(5L))
                .thenReturn(Optional.of(job));

        when(
                jobApplicationRepository.existsByUserIdAndJobId(
                        1L,
                        5L
                )
        )
                .thenReturn(true);

        assertThrows(
                DuplicateJobApplicationException.class,
                () -> jobApplicationService.createApplication(
                        userEmail,
                        request
                )
        );

        verify(userRepository)
                .findByEmail(userEmail);

        verify(jobRepository)
                .findById(5L);

        verify(
                jobApplicationRepository
        ).existsByUserIdAndJobId(1L, 5L);

        verify(
                jobApplicationRepository,
                never()
        ).save(any(JobApplication.class));

        verifyNoInteractions(jobApplicationMapper);
    }

    @Test
    void shouldGetAllApplications() {

        User user = new User(
                "Mihai",
                "Oprea",
                "getall@example.com",
                "encoded-password",
                "0712345678"
        );

        user.setId(1L);

        Company company = new Company(
                "Google",
                "Bucharest",
                "https://google.com"
        );

        company.setId(1L);

        Job job = new Job(
                "Java Backend Developer",
                "Java, Spring Boot",
                "Bucharest",
                EmploymentType.FULL_TIME,
                company
        );

        job.setId(5L);

        JobApplication application1 =
                new JobApplication(
                        user,
                        job,
                        ApplicationStatus.APPLIED
                );

        application1.setId(10L);

        JobApplication application2 =
                new JobApplication(
                        user,
                        job,
                        ApplicationStatus.UNDER_REVIEW
                );

        application2.setId(11L);

        JobApplicationResponse response1 =
                new JobApplicationResponse(
                        10L,
                        1L,
                        5L,
                        ApplicationStatus.APPLIED,
                        application1.getCreatedate()
                );

        JobApplicationResponse response2 =
                new JobApplicationResponse(
                        11L,
                        1L,
                        5L,
                        ApplicationStatus.UNDER_REVIEW,
                        application2.getCreatedate()
                );

        when(jobApplicationRepository.findAll())
                .thenReturn(List.of(
                        application1,
                        application2
                ));

        when(jobApplicationMapper.toResponse(application1))
                .thenReturn(response1);

        when(jobApplicationMapper.toResponse(application2))
                .thenReturn(response2);

        List<JobApplicationResponse> result =
                jobApplicationService.getAllApplications();

        assertEquals(2, result.size());

        assertEquals(
                10L,
                result.get(0).getId()
        );

        assertEquals(
                ApplicationStatus.APPLIED,
                result.get(0).getStatus()
        );

        assertEquals(
                11L,
                result.get(1).getId()
        );

        assertEquals(
                ApplicationStatus.UNDER_REVIEW,
                result.get(1).getStatus()
        );

        verify(jobApplicationRepository)
                .findAll();

        verify(jobApplicationMapper)
                .toResponse(application1);

        verify(jobApplicationMapper)
                .toResponse(application2);
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoApplications() {

        when(jobApplicationRepository.findAll())
                .thenReturn(List.of());

        List<JobApplicationResponse> result =
                jobApplicationService.getAllApplications();

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(jobApplicationRepository)
                .findAll();

        verifyNoInteractions(jobApplicationMapper);
    }

    @Test
    void shouldGetApplicationById() {

        User user = new User(
                "Mihai",
                "Oprea",
                "getbyid@example.com",
                "encoded-password",
                "0712345678"
        );

        user.setId(1L);

        Company company = new Company(
                "Google",
                "Bucharest",
                "https://google.com"
        );

        company.setId(1L);

        Job job = new Job(
                "Java Backend Developer",
                "Java, Spring Boot",
                "Bucharest",
                EmploymentType.FULL_TIME,
                company
        );

        job.setId(5L);

        JobApplication application =
                new JobApplication(
                        user,
                        job,
                        ApplicationStatus.APPLIED
                );

        application.setId(10L);

        JobApplicationResponse response =
                new JobApplicationResponse(
                        10L,
                        1L,
                        5L,
                        ApplicationStatus.APPLIED,
                        application.getCreatedate()
                );

        when(
                jobApplicationRepository.findById(10L)
        )
                .thenReturn(Optional.of(application));

        when(jobApplicationMapper.toResponse(application))
                .thenReturn(response);

        JobApplicationResponse result =
                jobApplicationService.getApplicationById(10L);

        assertEquals(10L, result.getId());
        assertEquals(1L, result.getUserId());
        assertEquals(5L, result.getJobId());

        assertEquals(
                ApplicationStatus.APPLIED,
                result.getStatus()
        );

        verify(jobApplicationRepository)
                .findById(10L);

        verify(jobApplicationMapper)
                .toResponse(application);
    }

    @Test
    void shouldThrowExceptionWhenApplicationDoesNotExist() {

        when(
                jobApplicationRepository.findById(999L)
        )
                .thenReturn(Optional.empty());

        assertThrows(
                JobApplicationNotFoundException.class,
                () -> jobApplicationService
                        .getApplicationById(999L)
        );

        verify(jobApplicationRepository)
                .findById(999L);

        verifyNoInteractions(jobApplicationMapper);
    }

    @Test
    void shouldUpdateApplicationStatus() {

        JobApplication application =
                new JobApplication(
                        null,
                        null,
                        ApplicationStatus.APPLIED
                );

        application.setId(10L);

        UpdateApplicationStatusRequest request =
                new UpdateApplicationStatusRequest(
                        ApplicationStatus.UNDER_REVIEW
                );

        JobApplicationResponse response =
                new JobApplicationResponse(
                        10L,
                        1L,
                        5L,
                        ApplicationStatus.UNDER_REVIEW,
                        application.getCreatedate()
                );

        when(
                jobApplicationRepository.findById(10L)
        )
                .thenReturn(Optional.of(application));

        when(
                jobApplicationRepository.save(application)
        )
                .thenReturn(application);

        when(
                jobApplicationMapper.toResponse(application)
        )
                .thenReturn(response);

        JobApplicationResponse result =
                jobApplicationService.updateApplicationStatus(
                        10L,
                        request
                );

        assertEquals(
                ApplicationStatus.UNDER_REVIEW,
                result.getStatus()
        );

        assertEquals(
                ApplicationStatus.UNDER_REVIEW,
                application.getStatus()
        );

        verify(jobApplicationRepository)
                .findById(10L);

        verify(jobApplicationRepository)
                .save(application);

        verify(jobApplicationMapper)
                .toResponse(application);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingApplication() {

        UpdateApplicationStatusRequest request =
                new UpdateApplicationStatusRequest(
                        ApplicationStatus.UNDER_REVIEW
                );

        when(
                jobApplicationRepository.findById(999L)
        )
                .thenReturn(Optional.empty());

        assertThrows(
                JobApplicationNotFoundException.class,
                () -> jobApplicationService
                        .updateApplicationStatus(
                                999L,
                                request
                        )
        );

        verify(jobApplicationRepository)
                .findById(999L);

        verify(
                jobApplicationRepository,
                never()
        ).save(any(JobApplication.class));

        verifyNoInteractions(jobApplicationMapper);
    }

    @Test
    void shouldAllowTransitionFromAppliedToUnderReview() {

        JobApplication application =
                new JobApplication(
                        null,
                        null,
                        ApplicationStatus.APPLIED
                );

        application.setId(10L);

        UpdateApplicationStatusRequest request =
                new UpdateApplicationStatusRequest(
                        ApplicationStatus.UNDER_REVIEW
                );

        JobApplicationResponse response =
                new JobApplicationResponse(
                        10L,
                        1L,
                        5L,
                        ApplicationStatus.UNDER_REVIEW,
                        application.getCreatedate()
                );

        when(
                jobApplicationRepository.findById(10L)
        )
                .thenReturn(Optional.of(application));

        when(
                jobApplicationRepository.save(application)
        )
                .thenReturn(application);

        when(
                jobApplicationMapper.toResponse(application)
        )
                .thenReturn(response);

        JobApplicationResponse result =
                jobApplicationService.updateApplicationStatus(
                        10L,
                        request
                );

        assertEquals(
                ApplicationStatus.UNDER_REVIEW,
                result.getStatus()
        );

        verify(jobApplicationRepository)
                .save(application);
    }

    @Test
    void shouldAllowTransitionFromUnderReviewToAccepted() {

        JobApplication application =
                new JobApplication(
                        null,
                        null,
                        ApplicationStatus.UNDER_REVIEW
                );

        application.setId(10L);

        UpdateApplicationStatusRequest request =
                new UpdateApplicationStatusRequest(
                        ApplicationStatus.ACCEPTED
                );

        JobApplicationResponse response =
                new JobApplicationResponse(
                        10L,
                        1L,
                        5L,
                        ApplicationStatus.ACCEPTED,
                        application.getCreatedate()
                );

        when(
                jobApplicationRepository.findById(10L)
        )
                .thenReturn(Optional.of(application));

        when(
                jobApplicationRepository.save(application)
        )
                .thenReturn(application);

        when(
                jobApplicationMapper.toResponse(application)
        )
                .thenReturn(response);

        JobApplicationResponse result =
                jobApplicationService.updateApplicationStatus(
                        10L,
                        request
                );

        assertEquals(
                ApplicationStatus.ACCEPTED,
                result.getStatus()
        );
    }

    @Test
    void shouldAllowTransitionFromUnderReviewToRejected() {

        JobApplication application =
                new JobApplication(
                        null,
                        null,
                        ApplicationStatus.UNDER_REVIEW
                );

        application.setId(10L);

        UpdateApplicationStatusRequest request =
                new UpdateApplicationStatusRequest(
                        ApplicationStatus.REJECTED
                );

        JobApplicationResponse response =
                new JobApplicationResponse(
                        10L,
                        1L,
                        5L,
                        ApplicationStatus.REJECTED,
                        application.getCreatedate()
                );

        when(
                jobApplicationRepository.findById(10L)
        )
                .thenReturn(Optional.of(application));

        when(
                jobApplicationRepository.save(application)
        )
                .thenReturn(application);

        when(
                jobApplicationMapper.toResponse(application)
        )
                .thenReturn(response);

        JobApplicationResponse result =
                jobApplicationService.updateApplicationStatus(
                        10L,
                        request
                );

        assertEquals(
                ApplicationStatus.REJECTED,
                result.getStatus()
        );
    }

    @Test
    void shouldRejectTransitionFromAppliedToAccepted() {

        JobApplication application =
                new JobApplication(
                        null,
                        null,
                        ApplicationStatus.APPLIED
                );

        application.setId(10L);

        UpdateApplicationStatusRequest request =
                new UpdateApplicationStatusRequest(
                        ApplicationStatus.ACCEPTED
                );

        when(
                jobApplicationRepository.findById(10L)
        )
                .thenReturn(Optional.of(application));

        assertThrows(
                InvalidApplicationStatusTransitionException.class,
                () -> jobApplicationService
                        .updateApplicationStatus(
                                10L,
                                request
                        )
        );

        verify(
                jobApplicationRepository,
                never()
        ).save(any(JobApplication.class));
    }

    @Test
    void shouldRejectTransitionFromAppliedToRejected() {

        JobApplication application =
                new JobApplication(
                        null,
                        null,
                        ApplicationStatus.APPLIED
                );

        application.setId(10L);

        UpdateApplicationStatusRequest request =
                new UpdateApplicationStatusRequest(
                        ApplicationStatus.REJECTED
                );

        when(
                jobApplicationRepository.findById(10L)
        )
                .thenReturn(Optional.of(application));

        assertThrows(
                InvalidApplicationStatusTransitionException.class,
                () -> jobApplicationService
                        .updateApplicationStatus(
                                10L,
                                request
                        )
        );

        verify(
                jobApplicationRepository,
                never()
        ).save(any(JobApplication.class));
    }

    @Test
    void shouldRejectTransitionFromAcceptedToRejected() {

        JobApplication application =
                new JobApplication(
                        null,
                        null,
                        ApplicationStatus.ACCEPTED
                );

        application.setId(10L);

        UpdateApplicationStatusRequest request =
                new UpdateApplicationStatusRequest(
                        ApplicationStatus.REJECTED
                );

        when(
                jobApplicationRepository.findById(10L)
        )
                .thenReturn(Optional.of(application));

        assertThrows(
                InvalidApplicationStatusTransitionException.class,
                () -> jobApplicationService
                        .updateApplicationStatus(
                                10L,
                                request
                        )
        );

        verify(
                jobApplicationRepository,
                never()
        ).save(any(JobApplication.class));
    }

    @Test
    void shouldRejectTransitionFromRejectedToAccepted() {

        JobApplication application =
                new JobApplication(
                        null,
                        null,
                        ApplicationStatus.REJECTED
                );

        application.setId(10L);

        UpdateApplicationStatusRequest request =
                new UpdateApplicationStatusRequest(
                        ApplicationStatus.ACCEPTED
                );

        when(
                jobApplicationRepository.findById(10L)
        )
                .thenReturn(Optional.of(application));

        assertThrows(
                InvalidApplicationStatusTransitionException.class,
                () -> jobApplicationService
                        .updateApplicationStatus(
                                10L,
                                request
                        )
        );

        verify(
                jobApplicationRepository,
                never()
        ).save(any(JobApplication.class));
    }

    @Test
    void shouldGetApplicationsByUserId() {

        User user = new User(
                "Mihai",
                "Oprea",
                "applications-user@example.com",
                "encoded-password",
                "0712345678"
        );

        user.setId(1L);

        Company company = new Company(
                "Google",
                "Bucharest",
                "https://google.com"
        );

        company.setId(1L);

        Job job1 = new Job(
                "Java Developer",
                "Java, Spring Boot",
                "Bucharest",
                EmploymentType.FULL_TIME,
                company
        );

        Job job2 = new Job(
                "C++ Developer",
                "C++, Linux",
                "Bucharest",
                EmploymentType.FULL_TIME,
                company
        );

        job1.setId(5L);
        job2.setId(6L);

        JobApplication application1 =
                new JobApplication(
                        user,
                        job1,
                        ApplicationStatus.APPLIED
                );

        application1.setId(10L);

        JobApplication application2 =
                new JobApplication(
                        user,
                        job2,
                        ApplicationStatus.UNDER_REVIEW
                );

        application2.setId(11L);

        JobApplicationResponse response1 =
                new JobApplicationResponse(
                        10L,
                        1L,
                        5L,
                        ApplicationStatus.APPLIED,
                        application1.getCreatedate()
                );

        JobApplicationResponse response2 =
                new JobApplicationResponse(
                        11L,
                        1L,
                        6L,
                        ApplicationStatus.UNDER_REVIEW,
                        application2.getCreatedate()
                );

        when(userRepository.existsById(1L))
                .thenReturn(true);

        when(jobApplicationRepository.findByUserId(1L))
                .thenReturn(List.of(
                        application1,
                        application2
                ));

        when(jobApplicationMapper.toResponse(application1))
                .thenReturn(response1);

        when(jobApplicationMapper.toResponse(application2))
                .thenReturn(response2);

        List<JobApplicationResponse> result =
                jobApplicationService.getApplicationsByUserId(1L);

        assertEquals(2, result.size());

        assertEquals(
                10L,
                result.get(0).getId()
        );

        assertEquals(
                11L,
                result.get(1).getId()
        );

        verify(userRepository)
                .existsById(1L);

        verify(jobApplicationRepository)
                .findByUserId(1L);
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoApplications() {

        when(userRepository.existsById(1L))
                .thenReturn(true);

        when(jobApplicationRepository.findByUserId(1L))
                .thenReturn(List.of());

        List<JobApplicationResponse> result =
                jobApplicationService.getApplicationsByUserId(1L);

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(userRepository)
                .existsById(1L);

        verify(jobApplicationRepository)
                .findByUserId(1L);

        verifyNoInteractions(jobApplicationMapper);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenGettingApplicationsByUserId() {

        when(userRepository.existsById(999L))
                .thenReturn(false);

        assertThrows(
                UserNotFoundException.class,
                () -> jobApplicationService
                        .getApplicationsByUserId(999L)
        );

        verify(userRepository)
                .existsById(999L);

        verifyNoInteractions(jobApplicationRepository);
        verifyNoInteractions(jobApplicationMapper);
    }

    @Test
    void shouldGetApplicationsByJobId() {

        User user1 = new User(
                "Mihai",
                "Oprea",
                "job-app-1@example.com",
                "encoded-password",
                "0712345678"
        );

        User user2 = new User(
                "John",
                "Doe",
                "job-app-2@example.com",
                "encoded-password",
                "0723456789"
        );

        user1.setId(1L);
        user2.setId(2L);

        Company company = new Company(
                "Google",
                "Bucharest",
                "https://google.com"
        );

        company.setId(1L);

        Job job = new Job(
                "Java Backend Developer",
                "Java, Spring Boot",
                "Bucharest",
                EmploymentType.FULL_TIME,
                company
        );

        job.setId(5L);

        JobApplication application1 =
                new JobApplication(
                        user1,
                        job,
                        ApplicationStatus.APPLIED
                );

        application1.setId(10L);

        JobApplication application2 =
                new JobApplication(
                        user2,
                        job,
                        ApplicationStatus.ACCEPTED
                );

        application2.setId(11L);

        JobApplicationResponse response1 =
                new JobApplicationResponse(
                        10L,
                        1L,
                        5L,
                        ApplicationStatus.APPLIED,
                        application1.getCreatedate()
                );

        JobApplicationResponse response2 =
                new JobApplicationResponse(
                        11L,
                        2L,
                        5L,
                        ApplicationStatus.ACCEPTED,
                        application2.getCreatedate()
                );

        when(jobRepository.existsById(5L))
                .thenReturn(true);

        when(jobApplicationRepository.findByJobId(5L))
                .thenReturn(List.of(
                        application1,
                        application2
                ));

        when(jobApplicationMapper.toResponse(application1))
                .thenReturn(response1);

        when(jobApplicationMapper.toResponse(application2))
                .thenReturn(response2);

        List<JobApplicationResponse> result =
                jobApplicationService.getApplicationsByJobId(5L);

        assertEquals(2, result.size());

        assertEquals(
                10L,
                result.get(0).getId()
        );

        assertEquals(
                11L,
                result.get(1).getId()
        );

        verify(jobRepository)
                .existsById(5L);

        verify(jobApplicationRepository)
                .findByJobId(5L);
    }

    @Test
    void shouldReturnEmptyListWhenJobHasNoApplications() {

        when(jobRepository.existsById(5L))
                .thenReturn(true);

        when(jobApplicationRepository.findByJobId(5L))
                .thenReturn(List.of());

        List<JobApplicationResponse> result =
                jobApplicationService.getApplicationsByJobId(5L);

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(jobRepository)
                .existsById(5L);

        verify(jobApplicationRepository)
                .findByJobId(5L);

        verifyNoInteractions(jobApplicationMapper);
    }

    @Test
    void shouldThrowJobNotFoundExceptionWhenGettingApplicationsByJobId() {

        when(jobRepository.existsById(999L))
                .thenReturn(false);

        assertThrows(
                JobNotFoundException.class,
                () -> jobApplicationService
                        .getApplicationsByJobId(999L)
        );

        verify(jobRepository)
                .existsById(999L);

        verifyNoInteractions(jobApplicationRepository);
        verifyNoInteractions(jobApplicationMapper);
    }
}