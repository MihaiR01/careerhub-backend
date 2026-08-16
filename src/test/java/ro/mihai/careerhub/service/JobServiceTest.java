package ro.mihai.careerhub.service;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import ro.mihai.careerhub.dto.request.CreateJobRequest;
import ro.mihai.careerhub.dto.response.JobResponse;
import ro.mihai.careerhub.entity.Company;
import ro.mihai.careerhub.entity.Job;
import ro.mihai.careerhub.enums.EmploymentType;
import ro.mihai.careerhub.exception.CompanyNotFoundException;
import ro.mihai.careerhub.exception.JobNotFoundException;
import ro.mihai.careerhub.mapper.JobMapper;
import ro.mihai.careerhub.repository.CompanyRepository;
import ro.mihai.careerhub.repository.JobRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private JobMapper jobMapper;

    private JobService jobService;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        jobService = new JobService(
                jobRepository,
                companyRepository,
                jobMapper
        );
    }

    @Test
    void shouldCreateJob() {

        Company company = new Company(
                "Google",
                "Bucharest",
                "https://google.com"
        );

        CreateJobRequest request = new CreateJobRequest();

        request.setTitle("Java Backend Developer");
        request.setTechnologies("Java, Spring Boot, PostgreSQL");
        request.setLocation("Bucharest");
        request.setEmploymentType(EmploymentType.FULL_TIME);
        request.setCompanyId(1L);

        Job job = new Job(
                "Java Backend Developer",
                "Java, Spring Boot, PostgreSQL",
                "Bucharest",
                EmploymentType.FULL_TIME,
                company
        );

        JobResponse response = new JobResponse(
                1L,
                "Java Backend Developer",
                "Java, Spring Boot, PostgreSQL",
                "Bucharest",
                EmploymentType.FULL_TIME,
                job.getCreatedate(),
                1L
        );

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));

        when(jobRepository.save(any(Job.class)))
                .thenReturn(job);

        when(jobMapper.toResponse(job))
                .thenReturn(response);

        JobResponse result =
                jobService.createJob(request);

        assertEquals(1L, result.getId());
        assertEquals(
                "Java Backend Developer",
                result.getTitle()
        );
        assertEquals(
                "Java, Spring Boot, PostgreSQL",
                result.getTechnologies()
        );
        assertEquals(
                "Bucharest",
                result.getLocation()
        );
        assertEquals(
                EmploymentType.FULL_TIME,
                result.getEmploymentType()
        );
        assertEquals(1L, result.getCompanyId());

        verify(companyRepository).findById(1L);
        verify(jobRepository).save(any(Job.class));
        verify(jobMapper).toResponse(job);
    }

    @Test
    void shouldThrowCompanyNotFoundExceptionWhenCreatingJob() {

        CreateJobRequest request = new CreateJobRequest();

        request.setTitle("Java Backend Developer");
        request.setTechnologies("Java, Spring Boot");
        request.setLocation("Bucharest");
        request.setEmploymentType(EmploymentType.FULL_TIME);
        request.setCompanyId(999L);

        when(companyRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                CompanyNotFoundException.class,
                () -> jobService.createJob(request)
        );

        verify(companyRepository).findById(999L);

        verify(
                jobRepository,
                never()
        ).save(any(Job.class));

        verifyNoInteractions(jobMapper);
    }

    @Test
    void shouldGetJobsWithFiltersAndPagination() {

        Company company = new Company(
                "Google",
                "Bucharest",
                "https://google.com"
        );

        Job job1 = new Job(
                "Java Backend Developer",
                "Java, Spring Boot",
                "Cluj-Napoca",
                EmploymentType.FULL_TIME,
                company
        );

        Job job2 = new Job(
                "Java Intern",
                "Java, Spring Boot",
                "Cluj-Napoca",
                EmploymentType.INTERNSHIP,
                company
        );

        JobResponse response1 = new JobResponse(
                1L,
                "Java Backend Developer",
                "Java, Spring Boot",
                "Cluj-Napoca",
                EmploymentType.FULL_TIME,
                null,
                1L
        );

        JobResponse response2 = new JobResponse(
                2L,
                "Java Intern",
                "Java, Spring Boot",
                "Cluj-Napoca",
                EmploymentType.INTERNSHIP,
                null,
                1L
        );

        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by("title").ascending()
        );

        Page<Job> page = new PageImpl<>(
                List.of(job1, job2),
                pageable,
                2
        );

        when(
                jobRepository.findAll(
                        any(Specification.class),
                        eq(pageable)
                )
        )
                .thenReturn(page);

        when(jobMapper.toResponse(job1))
                .thenReturn(response1);

        when(jobMapper.toResponse(job2))
                .thenReturn(response2);

        Page<JobResponse> result =
                jobService.getJobs(
                        "Cluj-Napoca",
                        null,
                        "Java",
                        pageable
                );

        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(0, result.getNumber());
        assertEquals(10, result.getSize());

        assertEquals(
                "Java Backend Developer",
                result.getContent().get(0).getTitle()
        );

        assertEquals(
                "Java Intern",
                result.getContent().get(1).getTitle()
        );

        verify(jobRepository).findAll(
                any(Specification.class),
                eq(pageable)
        );

        verify(jobMapper).toResponse(job1);
        verify(jobMapper).toResponse(job2);
    }

    @Test
    void shouldReturnEmptyPageWhenNoJobsMatchFilters() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Job> emptyPage = new PageImpl<>(
                List.of(),
                pageable,
                0
        );

        when(
                jobRepository.findAll(
                        any(Specification.class),
                        eq(pageable)
                )
        )
                .thenReturn(emptyPage);

        Page<JobResponse> result =
                jobService.getJobs(
                        "Cluj-Napoca",
                        EmploymentType.FULL_TIME,
                        "Rust",
                        pageable
                );

        assertEquals(0, result.getContent().size());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());

        verify(jobRepository).findAll(
                any(Specification.class),
                eq(pageable)
        );

        verifyNoInteractions(jobMapper);
    }

    @Test
    void shouldPaginateJobs() {

        Company company = new Company(
                "Google",
                "Bucharest",
                "https://google.com"
        );

        Job job1 = new Job(
                "Java Developer 1",
                "Java, Spring Boot",
                "Cluj-Napoca",
                EmploymentType.FULL_TIME,
                company
        );

        Job job2 = new Job(
                "Java Developer 2",
                "Java, Spring Boot",
                "Cluj-Napoca",
                EmploymentType.FULL_TIME,
                company
        );

        JobResponse response1 = new JobResponse(
                1L,
                "Java Developer 1",
                "Java, Spring Boot",
                "Cluj-Napoca",
                EmploymentType.FULL_TIME,
                null,
                1L
        );

        JobResponse response2 = new JobResponse(
                2L,
                "Java Developer 2",
                "Java, Spring Boot",
                "Cluj-Napoca",
                EmploymentType.FULL_TIME,
                null,
                1L
        );

        Pageable pageable = PageRequest.of(1, 2);

        Page<Job> page = new PageImpl<>(
                List.of(job1, job2),
                pageable,
                5
        );

        when(
                jobRepository.findAll(
                        any(Specification.class),
                        eq(pageable)
                )
        )
                .thenReturn(page);

        when(jobMapper.toResponse(job1))
                .thenReturn(response1);

        when(jobMapper.toResponse(job2))
                .thenReturn(response2);

        Page<JobResponse> result =
                jobService.getJobs(
                        null,
                        null,
                        null,
                        pageable
                );

        assertEquals(2, result.getContent().size());
        assertEquals(5, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
        assertEquals(1, result.getNumber());
        assertEquals(2, result.getSize());
    }

    @Test
    void shouldGetJobById() {

        Company company = new Company(
                "Nexttech",
                "Cluj-Napoca",
                "https://www.nexttech.ro"
        );

        Job job = new Job(
                "Java Backend Developer",
                "Java, Spring Boot, PostgreSQL",
                "Cluj-Napoca",
                EmploymentType.FULL_TIME,
                company
        );

        JobResponse response = new JobResponse(
                1L,
                "Java Backend Developer",
                "Java, Spring Boot, PostgreSQL",
                "Cluj-Napoca",
                EmploymentType.FULL_TIME,
                null,
                1L
        );

        when(jobRepository.findById(1L))
                .thenReturn(Optional.of(job));

        when(jobMapper.toResponse(job))
                .thenReturn(response);

        JobResponse result =
                jobService.getJobById(1L);

        assertEquals(1L, result.getId());
        assertEquals(
                "Java Backend Developer",
                result.getTitle()
        );
        assertEquals(
                "Java, Spring Boot, PostgreSQL",
                result.getTechnologies()
        );
        assertEquals(
                "Cluj-Napoca",
                result.getLocation()
        );
        assertEquals(
                EmploymentType.FULL_TIME,
                result.getEmploymentType()
        );
        assertEquals(1L, result.getCompanyId());

        verify(jobRepository).findById(1L);
        verify(jobMapper).toResponse(job);
    }

    @Test
    void shouldThrowExceptionWhenJobDoesNotExist() {

        when(jobRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                JobNotFoundException.class,
                () -> jobService.getJobById(999L)
        );

        verify(jobRepository).findById(999L);
        verifyNoInteractions(jobMapper);
    }
}