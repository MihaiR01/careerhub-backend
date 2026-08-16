package ro.mihai.careerhub.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;

import ro.mihai.careerhub.dto.request.CreateJobApplicationRequest;
import ro.mihai.careerhub.dto.request.UpdateApplicationStatusRequest;
import ro.mihai.careerhub.dto.response.JobApplicationResponse;
import ro.mihai.careerhub.service.JobApplicationService;

@RestController
@RequestMapping("/applications")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    public JobApplicationController(
            JobApplicationService jobApplicationService) {

        this.jobApplicationService = jobApplicationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobApplicationResponse createApplication(
            Authentication authentication,
            @Valid @RequestBody CreateJobApplicationRequest request) {

        String userEmail = authentication.getName();

        return jobApplicationService.createApplication(
                userEmail,
                request
        );
    }

    @GetMapping
    public List<JobApplicationResponse> getAllApplications() {

        return jobApplicationService.getAllApplications();
    }

    @GetMapping("/{id}")
    public JobApplicationResponse getApplicationById(
            @PathVariable Long id) {

        return jobApplicationService.getApplicationById(id);
    }

    @PutMapping("/{id}/status")
    public JobApplicationResponse updateApplicationStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateApplicationStatusRequest request) {

        return jobApplicationService.updateApplicationStatus(
                id,
                request
        );
    }

    @GetMapping("/user/{userId}")
    public List<JobApplicationResponse> getApplicationsByUserId(
            @PathVariable Long userId) {

        return jobApplicationService.getApplicationsByUserId(userId);
    }

    @GetMapping("/job/{jobId}")
    public List<JobApplicationResponse> getApplicationsByJobId(
            @PathVariable Long jobId) {

        return jobApplicationService.getApplicationsByJobId(jobId);
    }
}