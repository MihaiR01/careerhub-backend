package ro.mihai.careerhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ro.mihai.careerhub.entity.Job;

public interface JobRepository extends JpaRepository<Job, Long> {
}