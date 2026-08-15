package ro.mihai.careerhub.mapper;

import org.junit.jupiter.api.Test;

import ro.mihai.careerhub.dto.response.JobApplicationResponse;
import ro.mihai.careerhub.entity.Company;
import ro.mihai.careerhub.entity.Job;
import ro.mihai.careerhub.entity.JobApplication;
import ro.mihai.careerhub.entity.User;
import ro.mihai.careerhub.enums.ApplicationStatus;
import ro.mihai.careerhub.enums.EmploymentType;

import static org.junit.jupiter.api.Assertions.*;

class JobApplicationMapperTest {

    private final JobApplicationMapper jobApplicationMapper =
            new JobApplicationMapper();

    @Test
    void shouldMapJobApplicationToResponse() {

        User user = new User(
                "Mihai",
                "Oprea",
                "mappertest@example.com",
                "password123",
                "0712345678"
        );

        Company company = new Company(
                "Google",
                "Bucharest",
                "https://google.com"
        );

        Job job = new Job(
                "Java Backend Developer",
                "Java, Spring Boot, PostgreSQL",
                "Bucharest",
                EmploymentType.FULL_TIME,
                company
        );

        JobApplication application = new JobApplication(
                user,
                job,
                ApplicationStatus.APPLIED
        );

        JobApplicationResponse response =
                jobApplicationMapper.toResponse(application);

        assertEquals(
                user.getId(),
                response.getUserId()
        );

        assertEquals(
                job.getId(),
                response.getJobId()
        );

        assertEquals(
                ApplicationStatus.APPLIED,
                response.getStatus()
        );

        assertNotNull(response.getCreatedate());
    }
}