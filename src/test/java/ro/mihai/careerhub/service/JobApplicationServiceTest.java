package ro.mihai.careerhub.service;

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
import ro.mihai.careerhub.exception.InvalidApplicationStatusTransitionException;
import ro.mihai.careerhub.exception.JobApplicationNotFoundException;
import ro.mihai.careerhub.exception.JobNotFoundException;
import ro.mihai.careerhub.exception.UserNotFoundException;
import ro.mihai.careerhub.mapper.JobApplicationMapper;
import ro.mihai.careerhub.repository.JobApplicationRepository;
import ro.mihai.careerhub.repository.JobRepository;
import ro.mihai.careerhub.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

        User user = new User(
                "Mihai",
                "Oprea",
                "serviceapplication@example.com",
                "password123",
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

        CreateJobApplicationRequest request =
                new CreateJobApplicationRequest(
                        1L,
                        5L
                );

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

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(jobRepository.findById(5L))
                .thenReturn(Optional.of(job));

        when(jobApplicationRepository.save(any(JobApplication.class)))
                .thenReturn(application);

        when(jobApplicationMapper.toResponse(application))
                .thenReturn(response);

        JobApplicationResponse result =
                jobApplicationService.createApplication(request);

        assertEquals(10L, result.getId());
        assertEquals(1L, result.getUserId());
        assertEquals(5L, result.getJobId());
        assertEquals(
                ApplicationStatus.APPLIED,
                result.getStatus()
        );

        verify(userRepository).findById(1L);
        verify(jobRepository).findById(5L);
        verify(jobApplicationRepository)
                .save(any(JobApplication.class));
        verify(jobApplicationMapper)
                .toResponse(application);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {

        CreateJobApplicationRequest request =
                new CreateJobApplicationRequest(
                        999L,
                        5L
                );

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> jobApplicationService.createApplication(request)
        );

        verify(userRepository).findById(999L);

        verifyNoInteractions(jobRepository);
        verifyNoInteractions(jobApplicationRepository);
        verifyNoInteractions(jobApplicationMapper);
    }

    @Test
    void shouldThrowJobNotFoundExceptionWhenJobDoesNotExist() {

        User user = new User(
                "Mihai",
                "Oprea",
                "jobnotfound@example.com",
                "password123",
                "0712345678"
        );

        user.setId(1L);

        CreateJobApplicationRequest request =
                new CreateJobApplicationRequest(
                        1L,
                        999L
                );

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(jobRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                JobNotFoundException.class,
                () -> jobApplicationService.createApplication(request)
        );

        verify(userRepository).findById(1L);
        verify(jobRepository).findById(999L);

        verifyNoInteractions(jobApplicationRepository);
        verifyNoInteractions(jobApplicationMapper);
    }

    @Test
    void shouldGetAllApplications() {

        User user = new User(
                "Mihai",
                "Oprea",
                "getall@example.com",
                "password123",
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
                .thenReturn(List.of(application1, application2));

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

        verify(jobApplicationRepository).findAll();
        verify(jobApplicationMapper).toResponse(application1);
        verify(jobApplicationMapper).toResponse(application2);
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoApplications() {

        when(jobApplicationRepository.findAll())
                .thenReturn(List.of());

        List<JobApplicationResponse> result =
                jobApplicationService.getAllApplications();

        assertEquals(0, result.size());

        verify(jobApplicationRepository).findAll();

        verifyNoInteractions(jobApplicationMapper);
    }

    @Test
    void shouldGetApplicationById() {

        User user = new User(
                "Mihai",
                "Oprea",
                "getbyid@example.com",
                "password123",
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

        when(jobApplicationRepository.findById(10L))
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

        when(jobApplicationRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                JobApplicationNotFoundException.class,
                () -> jobApplicationService.getApplicationById(999L)
        );

        verify(jobApplicationRepository)
                .findById(999L);

        verifyNoInteractions(jobApplicationMapper);
    }

    @Test
    void shouldUpdateApplicationStatus() {

        User user = new User(
                "Mihai",
                "Oprea",
                "updatestatus@example.com",
                "password123",
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

        when(jobApplicationRepository.findById(10L))
                .thenReturn(Optional.of(application));

        when(jobApplicationRepository.save(application))
                .thenReturn(application);

        when(jobApplicationMapper.toResponse(application))
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

        when(jobApplicationRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                JobApplicationNotFoundException.class,
                () -> jobApplicationService.updateApplicationStatus(
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

        when(jobApplicationRepository.findById(10L))
                .thenReturn(Optional.of(application));

        when(jobApplicationRepository.save(application))
                .thenReturn(application);

        when(jobApplicationMapper.toResponse(application))
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

        verify(jobApplicationRepository).findById(10L);
        verify(jobApplicationRepository).save(application);
        verify(jobApplicationMapper).toResponse(application);
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

        when(jobApplicationRepository.findById(10L))
                .thenReturn(Optional.of(application));

        when(jobApplicationRepository.save(application))
                .thenReturn(application);

        when(jobApplicationMapper.toResponse(application))
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

        assertEquals(
                ApplicationStatus.ACCEPTED,
                application.getStatus()
        );

        verify(jobApplicationRepository).findById(10L);
        verify(jobApplicationRepository).save(application);
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

        when(jobApplicationRepository.findById(10L))
                .thenReturn(Optional.of(application));

        when(jobApplicationRepository.save(application))
                .thenReturn(application);

        when(jobApplicationMapper.toResponse(application))
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

        assertEquals(
                ApplicationStatus.REJECTED,
                application.getStatus()
        );

        verify(jobApplicationRepository).findById(10L);
        verify(jobApplicationRepository).save(application);
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

        when(jobApplicationRepository.findById(10L))
                .thenReturn(Optional.of(application));

        assertThrows(
                InvalidApplicationStatusTransitionException.class,
                () -> jobApplicationService.updateApplicationStatus(
                        10L,
                        request
                )
        );

        assertEquals(
                ApplicationStatus.APPLIED,
                application.getStatus()
        );

        verify(jobApplicationRepository).findById(10L);

        verify(
                jobApplicationRepository,
                never()
        ).save(any(JobApplication.class));

        verifyNoInteractions(jobApplicationMapper);
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

        when(jobApplicationRepository.findById(10L))
                .thenReturn(Optional.of(application));

        assertThrows(
                InvalidApplicationStatusTransitionException.class,
                () -> jobApplicationService.updateApplicationStatus(
                        10L,
                        request
                )
        );

        assertEquals(
                ApplicationStatus.APPLIED,
                application.getStatus()
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

        when(jobApplicationRepository.findById(10L))
                .thenReturn(Optional.of(application));

        assertThrows(
                InvalidApplicationStatusTransitionException.class,
                () -> jobApplicationService.updateApplicationStatus(
                        10L,
                        request
                )
        );

        assertEquals(
                ApplicationStatus.ACCEPTED,
                application.getStatus()
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

        when(jobApplicationRepository.findById(10L))
                .thenReturn(Optional.of(application));

        assertThrows(
                InvalidApplicationStatusTransitionException.class,
                () -> jobApplicationService.updateApplicationStatus(
                        10L,
                        request
                )
        );

        assertEquals(
                ApplicationStatus.REJECTED,
                application.getStatus()
        );

        verify(
                jobApplicationRepository,
                never()
        ).save(any(JobApplication.class));
    }
}