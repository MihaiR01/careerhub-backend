package ro.mihai.careerhub.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ro.mihai.careerhub.dto.request.CreateJobRequest;
import ro.mihai.careerhub.dto.response.JobResponse;
import ro.mihai.careerhub.entity.Company;
import ro.mihai.careerhub.entity.Job;
import ro.mihai.careerhub.exception.CompanyNotFoundException;
import ro.mihai.careerhub.exception.JobNotFoundException;
import ro.mihai.careerhub.mapper.JobMapper;
import ro.mihai.careerhub.repository.CompanyRepository;
import ro.mihai.careerhub.repository.JobRepository;

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

    public List<JobResponse> getAllJobs() {

        return jobRepository.findAll()
                .stream()
                .map(jobMapper::toResponse)
                .toList();
    }

    public JobResponse getJobById(Long id) {

        Job job = jobRepository
                .findById(id)
                .orElseThrow(() -> new JobNotFoundException(id));

        return jobMapper.toResponse(job);
    }
}