package ro.mihai.careerhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ro.mihai.careerhub.entity.JobApplication;

public interface JobApplicationRepository
        extends JpaRepository<JobApplication, Long> {
}