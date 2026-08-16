package ro.mihai.careerhub.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ro.mihai.careerhub.dto.request.CreateJobApplicationRequest;
import ro.mihai.careerhub.dto.request.UpdateApplicationStatusRequest;
import ro.mihai.careerhub.dto.response.JobApplicationResponse;
import ro.mihai.careerhub.entity.Job;
import ro.mihai.careerhub.entity.JobApplication;
import ro.mihai.careerhub.entity.User;
import ro.mihai.careerhub.enums.ApplicationStatus;
import ro.mihai.careerhub.exception.DuplicateJobApplicationException;
import ro.mihai.careerhub.exception.InvalidApplicationStatusTransitionException;
import ro.mihai.careerhub.exception.JobApplicationNotFoundException;
import ro.mihai.careerhub.exception.JobNotFoundException;
import ro.mihai.careerhub.exception.UserNotFoundException;
import ro.mihai.careerhub.mapper.JobApplicationMapper;
import ro.mihai.careerhub.repository.JobApplicationRepository;
import ro.mihai.careerhub.repository.JobRepository;
import ro.mihai.careerhub.repository.UserRepository;

@Service
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final JobApplicationMapper jobApplicationMapper;

    public JobApplicationService(
            JobApplicationRepository jobApplicationRepository,
            UserRepository userRepository,
            JobRepository jobRepository,
            JobApplicationMapper jobApplicationMapper) {

        this.jobApplicationRepository = jobApplicationRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.jobApplicationMapper = jobApplicationMapper;
    }

    public JobApplicationResponse createApplication(
            String userEmail,
            CreateJobApplicationRequest request) {

        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(
                        () -> new UserNotFoundException(userEmail)
                );

        Job job = jobRepository
                .findById(request.getJobId())
                .orElseThrow(
                        () -> new JobNotFoundException(
                                request.getJobId()
                        )
                );

        if (jobApplicationRepository.existsByUserIdAndJobId(
                user.getId(),
                job.getId())) {

            throw new DuplicateJobApplicationException(
                    user.getId(),
                    job.getId()
            );
        }

        JobApplication application = new JobApplication(
                user,
                job,
                ApplicationStatus.APPLIED
        );

        JobApplication savedApplication =
                jobApplicationRepository.save(application);

        return jobApplicationMapper.toResponse(
                savedApplication
        );
    }

    public List<JobApplicationResponse> getAllApplications() {

        return jobApplicationRepository.findAll()
                .stream()
                .map(jobApplicationMapper::toResponse)
                .toList();
    }

    public JobApplicationResponse getApplicationById(Long id) {

        JobApplication application =
                jobApplicationRepository.findById(id)
                        .orElseThrow(
                                () -> new JobApplicationNotFoundException(
                                        id
                                )
                        );

        return jobApplicationMapper.toResponse(application);
    }

    public JobApplicationResponse updateApplicationStatus(
            Long id,
            UpdateApplicationStatusRequest request) {

        JobApplication application =
                jobApplicationRepository.findById(id)
                        .orElseThrow(
                                () -> new JobApplicationNotFoundException(
                                        id
                                )
                        );

        ApplicationStatus currentStatus =
                application.getStatus();

        ApplicationStatus nextStatus =
                request.getStatus();

        if (!isValidStatusTransition(
                currentStatus,
                nextStatus)) {

            throw new InvalidApplicationStatusTransitionException(
                    currentStatus,
                    nextStatus
            );
        }

        application.setStatus(nextStatus);

        JobApplication updatedApplication =
                jobApplicationRepository.save(application);

        return jobApplicationMapper.toResponse(
                updatedApplication
        );
    }

    public List<JobApplicationResponse> getApplicationsByUserId(
            Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        return jobApplicationRepository.findByUserId(userId)
                .stream()
                .map(jobApplicationMapper::toResponse)
                .toList();
    }

    public List<JobApplicationResponse> getApplicationsByJobId(
            Long jobId) {

        if (!jobRepository.existsById(jobId)) {
            throw new JobNotFoundException(jobId);
        }

        return jobApplicationRepository.findByJobId(jobId)
                .stream()
                .map(jobApplicationMapper::toResponse)
                .toList();
    }

    private boolean isValidStatusTransition(
            ApplicationStatus currentStatus,
            ApplicationStatus nextStatus) {

        return switch (currentStatus) {

            case APPLIED ->
                    nextStatus == ApplicationStatus.UNDER_REVIEW;

            case UNDER_REVIEW ->
                    nextStatus == ApplicationStatus.ACCEPTED
                            || nextStatus == ApplicationStatus.REJECTED;

            case ACCEPTED, REJECTED ->
                    false;
        };
    }
}