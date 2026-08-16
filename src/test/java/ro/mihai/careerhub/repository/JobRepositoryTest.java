package ro.mihai.careerhub.repository;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import ro.mihai.careerhub.TestcontainersConfiguration;
import ro.mihai.careerhub.entity.Company;
import ro.mihai.careerhub.entity.Job;
import ro.mihai.careerhub.enums.EmploymentType;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import(TestcontainersConfiguration.class)
class JobRepositoryTest {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @BeforeEach
    void cleanDatabase() {

        jobApplicationRepository.deleteAll();
        jobRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindJob() {

        Company company = new Company(
                "Google",
                "Bucharest",
                "https://google.com"
        );

        company = companyRepository.save(company);

        Job job = new Job(
                "Java Backend Developer",
                "Java, Spring Boot, PostgreSQL, Docker",
                "Bucharest",
                EmploymentType.FULL_TIME,
                company
        );

        Job savedJob = jobRepository.save(job);

        assertNotNull(savedJob.getId());
        assertEquals(
                "Java Backend Developer",
                savedJob.getTitle()
        );
        assertEquals(
                "Java, Spring Boot, PostgreSQL, Docker",
                savedJob.getTechnologies()
        );
        assertEquals(
                "Bucharest",
                savedJob.getLocation()
        );
        assertEquals(
                EmploymentType.FULL_TIME,
                savedJob.getEmploymentType()
        );
        assertNotNull(savedJob.getCreatedate());
        assertEquals(
                company.getId(),
                savedJob.getCompany().getId()
        );
    }

    @Test
    void shouldFilterJobsByLocation() {

        Company company = new Company(
                "Google",
                "Bucharest",
                "https://google.com"
        );

        company = companyRepository.save(company);

        Job clujJob = new Job(
                "Java Developer",
                "Java, Spring Boot",
                "Cluj-Napoca",
                EmploymentType.FULL_TIME,
                company
        );

        Job bucharestJob = new Job(
                "C++ Developer",
                "C++, Linux",
                "Bucharest",
                EmploymentType.FULL_TIME,
                company
        );

        jobRepository.save(clujJob);
        jobRepository.save(bucharestJob);

        Specification<Job> specification =
                JobSpecifications.hasLocation(
                        "Cluj-Napoca"
                );

        List<Job> result =
                jobRepository.findAll(specification);

        assertEquals(1, result.size());
        assertEquals(
                "Cluj-Napoca",
                result.get(0).getLocation()
        );
    }

    @Test
    void shouldFilterJobsByEmploymentType() {

        Company company = new Company(
                "Google",
                "Bucharest",
                "https://google.com"
        );

        company = companyRepository.save(company);

        Job fullTimeJob = new Job(
                "Java Developer",
                "Java, Spring Boot",
                "Bucharest",
                EmploymentType.FULL_TIME,
                company
        );

        Job internshipJob = new Job(
                "Java Intern",
                "Java, Spring Boot",
                "Bucharest",
                EmploymentType.INTERNSHIP,
                company
        );

        jobRepository.save(fullTimeJob);
        jobRepository.save(internshipJob);

        Specification<Job> specification =
                JobSpecifications.hasEmploymentType(
                        EmploymentType.FULL_TIME
                );

        List<Job> result =
                jobRepository.findAll(specification);

        assertEquals(1, result.size());
        assertEquals(
                EmploymentType.FULL_TIME,
                result.get(0).getEmploymentType()
        );
    }

    @Test
    void shouldFilterJobsByTechnology() {

        Company company = new Company(
                "Google",
                "Bucharest",
                "https://google.com"
        );

        company = companyRepository.save(company);

        Job javaJob = new Job(
                "Java Developer",
                "Java, Spring Boot, PostgreSQL",
                "Bucharest",
                EmploymentType.FULL_TIME,
                company
        );

        Job cppJob = new Job(
                "C++ Developer",
                "C++, Linux, Git",
                "Bucharest",
                EmploymentType.FULL_TIME,
                company
        );

        jobRepository.save(javaJob);
        jobRepository.save(cppJob);

        Specification<Job> specification =
                JobSpecifications.hasTechnology("Java");

        List<Job> result =
                jobRepository.findAll(specification);

        assertEquals(1, result.size());
        assertEquals(
                "Java Developer",
                result.get(0).getTitle()
        );
    }

    @Test
    void shouldFilterJobsByMultipleCriteria() {

        Company company = new Company(
                "Google",
                "Bucharest",
                "https://google.com"
        );

        company = companyRepository.save(company);

        Job matchingJob = new Job(
                "Java Backend Developer",
                "Java, Spring Boot, PostgreSQL",
                "Cluj-Napoca",
                EmploymentType.FULL_TIME,
                company
        );

        Job wrongLocation = new Job(
                "Java Developer",
                "Java, Spring Boot",
                "Bucharest",
                EmploymentType.FULL_TIME,
                company
        );

        Job wrongType = new Job(
                "Java Intern",
                "Java, Spring Boot",
                "Cluj-Napoca",
                EmploymentType.INTERNSHIP,
                company
        );

        Job wrongTechnology = new Job(
                "C++ Developer",
                "C++, Linux",
                "Cluj-Napoca",
                EmploymentType.FULL_TIME,
                company
        );

        jobRepository.save(matchingJob);
        jobRepository.save(wrongLocation);
        jobRepository.save(wrongType);
        jobRepository.save(wrongTechnology);

        Specification<Job> specification =
                JobSpecifications.hasLocation(
                        "Cluj-Napoca"
                )
                .and(
                        JobSpecifications.hasEmploymentType(
                                EmploymentType.FULL_TIME
                        )
                )
                .and(
                        JobSpecifications.hasTechnology(
                                "Java"
                        )
                );

        List<Job> result =
                jobRepository.findAll(specification);

        assertEquals(1, result.size());
        assertEquals(
                "Java Backend Developer",
                result.get(0).getTitle()
        );
    }

    @Test
    void shouldReturnAllJobsWhenNoFiltersAreProvided() {

        Company company = new Company(
                "Google",
                "Bucharest",
                "https://google.com"
        );

        company = companyRepository.save(company);

        Job job1 = new Job(
                "Java Developer",
                "Java, Spring Boot",
                "Cluj-Napoca",
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

        jobRepository.save(job1);
        jobRepository.save(job2);

        List<Job> result = jobRepository.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void shouldPaginateJobs() {

        Company company = new Company(
                "Google",
                "Bucharest",
                "https://google.com"
        );

        company = companyRepository.save(company);

        for (int i = 1; i <= 5; i++) {

            Job job = new Job(
                    "Java Developer " + i,
                    "Java, Spring Boot",
                    "Cluj-Napoca",
                    EmploymentType.FULL_TIME,
                    company
            );

            jobRepository.save(job);
        }

        Page<Job> result =
                jobRepository.findAll(
                        PageRequest.of(0, 2)
                );

        assertEquals(2, result.getContent().size());
        assertEquals(5, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
        assertEquals(0, result.getNumber());
        assertEquals(2, result.getSize());
    }

    @Test
    void shouldSortJobsByTitle() {

        Company company = new Company(
                "Google",
                "Bucharest",
                "https://google.com"
        );

        company = companyRepository.save(company);

        jobRepository.save(
                new Job(
                        "Java Developer",
                        "Java",
                        "Cluj-Napoca",
                        EmploymentType.FULL_TIME,
                        company
                )
        );

        jobRepository.save(
                new Job(
                        "Backend Developer",
                        "Java",
                        "Cluj-Napoca",
                        EmploymentType.FULL_TIME,
                        company
                )
        );

        Page<Job> result =
                jobRepository.findAll(
                        PageRequest.of(
                                0,
                                10,
                                Sort.by("title").ascending()
                        )
                );

        assertEquals(
                "Backend Developer",
                result.getContent()
                        .get(0)
                        .getTitle()
        );

        assertEquals(
                "Java Developer",
                result.getContent()
                        .get(1)
                        .getTitle()
        );
    }
}