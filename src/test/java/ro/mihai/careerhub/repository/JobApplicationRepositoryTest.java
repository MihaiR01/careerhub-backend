package ro.mihai.careerhub.repository;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import ro.mihai.careerhub.entity.Company;
import ro.mihai.careerhub.entity.Job;
import ro.mihai.careerhub.entity.JobApplication;
import ro.mihai.careerhub.entity.User;
import ro.mihai.careerhub.enums.ApplicationStatus;
import ro.mihai.careerhub.enums.EmploymentType;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class JobApplicationRepositoryTest {

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobRepository jobRepository;

    @BeforeEach
    void cleanDatabase() {
        jobApplicationRepository.deleteAll();
        jobRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindJobApplication() {

        User user = new User(
                "Mihai",
                "Oprea",
                "applicationtest@example.com",
                "password123",
                "0712345678"
        );

        user = userRepository.save(user);

        Company company = new Company(
                "Google",
                "Bucharest",
                "https://google.com"
        );

        company = companyRepository.save(company);

        Job job = new Job(
                "Java Backend Developer",
                "Java, Spring Boot, PostgreSQL",
                "Bucharest",
                EmploymentType.FULL_TIME,
                company
        );

        job = jobRepository.save(job);

        JobApplication application = new JobApplication(
                user,
                job,
                ApplicationStatus.APPLIED
        );

        JobApplication saved =
                jobApplicationRepository.save(application);

        Optional<JobApplication> result =
                jobApplicationRepository.findById(saved.getId());

        assertTrue(result.isPresent());

        JobApplication found = result.get();

        assertEquals(saved.getId(), found.getId());
        assertEquals(user.getId(), found.getUser().getId());
        assertEquals(job.getId(), found.getJob().getId());

        assertEquals(
                ApplicationStatus.APPLIED,
                found.getStatus()
        );

        assertNotNull(found.getCreatedate());
    }

    @Test
    void shouldFindApplicationsByUserId() {

        User user = new User(
                "Mihai",
                "Oprea",
                "findbyuser@example.com",
                "password123",
                "0712345678"
        );

        user = userRepository.save(user);

        Company company = new Company(
                "Google",
                "Bucharest",
                "https://google.com"
        );

        company = companyRepository.save(company);

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

        job1 = jobRepository.save(job1);
        job2 = jobRepository.save(job2);

        JobApplication application1 =
                new JobApplication(
                        user,
                        job1,
                        ApplicationStatus.APPLIED
                );

        JobApplication application2 =
                new JobApplication(
                        user,
                        job2,
                        ApplicationStatus.UNDER_REVIEW
                );

        jobApplicationRepository.save(application1);
        jobApplicationRepository.save(application2);

        List<JobApplication> result =
                jobApplicationRepository.findByUserId(
                        user.getId()
                );

        assertEquals(2, result.size());

        assertEquals(
                user.getId(),
                result.get(0).getUser().getId()
        );

        assertEquals(
                user.getId(),
                result.get(1).getUser().getId()
        );
    }

    @Test
    void shouldFindApplicationsByJobId() {

        User user1 = new User(
                "Mihai",
                "Oprea",
                "findbyjob1@example.com",
                "password123",
                "0712345678"
        );

        User user2 = new User(
                "John",
                "Doe",
                "findbyjob2@example.com",
                "password456",
                "0723456789"
        );

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        Company company = new Company(
                "Google",
                "Bucharest",
                "https://google.com"
        );

        company = companyRepository.save(company);

        Job job = new Job(
                "Java Backend Developer",
                "Java, Spring Boot",
                "Bucharest",
                EmploymentType.FULL_TIME,
                company
        );

        job = jobRepository.save(job);

        JobApplication application1 =
                new JobApplication(
                        user1,
                        job,
                        ApplicationStatus.APPLIED
                );

        JobApplication application2 =
                new JobApplication(
                        user2,
                        job,
                        ApplicationStatus.ACCEPTED
                );

        jobApplicationRepository.save(application1);
        jobApplicationRepository.save(application2);

        List<JobApplication> result =
                jobApplicationRepository.findByJobId(
                        job.getId()
                );

        assertEquals(2, result.size());

        assertEquals(
                job.getId(),
                result.get(0).getJob().getId()
        );

        assertEquals(
                job.getId(),
                result.get(1).getJob().getId()
        );
    }

    @Test
    void shouldReturnTrueWhenUserAlreadyAppliedToJob() {

        User user = new User(
                "Mihai",
                "Oprea",
                "duplicate-check@example.com",
                "password123",
                "0712345678"
        );

        user = userRepository.save(user);

        Company company = new Company(
                "Google",
                "Bucharest",
                "https://google.com"
        );

        company = companyRepository.save(company);

        Job job = new Job(
                "Java Developer",
                "Java, Spring Boot",
                "Bucharest",
                EmploymentType.FULL_TIME,
                company
        );

        job = jobRepository.save(job);

        JobApplication application =
                new JobApplication(
                        user,
                        job,
                        ApplicationStatus.APPLIED
                );

        jobApplicationRepository.save(application);

        boolean result =
                jobApplicationRepository.existsByUserIdAndJobId(
                        user.getId(),
                        job.getId()
                );

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenUserHasNotAppliedToJob() {

        User user = new User(
                "Mihai",
                "Oprea",
                "no-duplicate@example.com",
                "password123",
                "0712345678"
        );

        user = userRepository.save(user);

        Company company = new Company(
                "Google",
                "Bucharest",
                "https://google.com"
        );

        company = companyRepository.save(company);

        Job job = new Job(
                "Java Developer",
                "Java, Spring Boot",
                "Bucharest",
                EmploymentType.FULL_TIME,
                company
        );

        job = jobRepository.save(job);

        boolean result =
                jobApplicationRepository.existsByUserIdAndJobId(
                        user.getId(),
                        job.getId()
                );

        assertFalse(result);
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoApplications() {

        User user = new User(
                "Mihai",
                "Oprea",
                "empty-user@example.com",
                "password123",
                "0712345678"
        );

        user = userRepository.save(user);

        List<JobApplication> result =
                jobApplicationRepository.findByUserId(
                        user.getId()
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenJobHasNoApplications() {

        Company company = new Company(
                "Google",
                "Bucharest",
                "https://google.com"
        );

        company = companyRepository.save(company);

        Job job = new Job(
                "Java Developer",
                "Java, Spring Boot",
                "Bucharest",
                EmploymentType.FULL_TIME,
                company
        );

        job = jobRepository.save(job);

        List<JobApplication> result =
                jobApplicationRepository.findByJobId(
                        job.getId()
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}