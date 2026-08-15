package ro.mihai.careerhub.repository;

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

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JobApplicationRepositoryTest {

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobRepository jobRepository;

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
}