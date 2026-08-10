package ro.mihai.careerhub.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import ro.mihai.careerhub.entity.Company;
import ro.mihai.careerhub.entity.Job;
import ro.mihai.careerhub.enums.EmploymentType;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JobRepositoryTest {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void shouldSaveAndFindJob() {

        Company company = new Company(
                "Nexttech",
                "Cluj-Napoca",
                "https://www.nexttech.ro"
        );

        Company savedCompany = companyRepository.save(company);

        Job job = new Job(
                "Java Backend Developer",
                "Java, Spring Boot, PostgreSQL, Docker",
                "Cluj-Napoca",
                EmploymentType.FULL_TIME,
                savedCompany
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
                "Cluj-Napoca",
                savedJob.getLocation()
        );

        assertEquals(
                EmploymentType.FULL_TIME,
                savedJob.getEmploymentType()
        );

        assertNotNull(savedJob.getCreatedate());

        assertEquals(
                savedCompany.getId(),
                savedJob.getCompany().getId()
        );
    }
}