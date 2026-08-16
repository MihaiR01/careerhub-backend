package ro.mihai.careerhub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ro.mihai.careerhub.entity.JobApplication;

public interface JobApplicationRepository
        extends JpaRepository<JobApplication, Long> {

    boolean existsByUserIdAndJobId(Long userId, Long jobId);

    List<JobApplication> findByUserId(Long userId);

    List<JobApplication> findByJobId(Long jobId);
}