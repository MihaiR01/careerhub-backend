package ro.mihai.careerhub.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

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
import ro.mihai.careerhub.repository.JobSpecifications;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final JobMapper jobMapper;

    public JobService(
            JobRepository jobRepository,
            CompanyRepository companyRepository,
            JobMapper jobMapper) {

        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.jobMapper = jobMapper;
    }

    public JobResponse createJob(CreateJobRequest request) {

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(
                        () -> new CompanyNotFoundException(
                                request.getCompanyId()
                        )
                );

        Job job = new Job(
                request.getTitle(),
                request.getTechnologies(),
                request.getLocation(),
                request.getEmploymentType(),
                company
        );

        Job savedJob = jobRepository.save(job);

        return jobMapper.toResponse(savedJob);
    }

    public Page<JobResponse> getJobs(
            String location,
            EmploymentType employmentType,
            String technologies,
            Pageable pageable) {

        Specification<Job> specification =
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.conjunction();

        if (location != null && !location.isBlank()) {
            specification = specification.and(
                    JobSpecifications.hasLocation(location)
            );
        }

        if (employmentType != null) {
            specification = specification.and(
                    JobSpecifications.hasEmploymentType(
                            employmentType
                    )
            );
        }

        if (technologies != null && !technologies.isBlank()) {
            specification = specification.and(
                    JobSpecifications.hasTechnology(
                            technologies
                    )
            );
        }

        return jobRepository
                .findAll(specification, pageable)
                .map(jobMapper::toResponse);
    }

    public JobResponse getJobById(Long id) {

        Job job = jobRepository
                .findById(id)
                .orElseThrow(() -> new JobNotFoundException(id));

        return jobMapper.toResponse(job);
    }
}