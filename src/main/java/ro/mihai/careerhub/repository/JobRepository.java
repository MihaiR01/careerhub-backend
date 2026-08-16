package ro.mihai.careerhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import ro.mihai.careerhub.entity.Job;

public interface JobRepository
        extends JpaRepository<Job, Long>,
                JpaSpecificationExecutor<Job> {
}