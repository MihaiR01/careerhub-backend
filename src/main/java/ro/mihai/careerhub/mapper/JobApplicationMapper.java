package ro.mihai.careerhub.mapper;

import org.springframework.stereotype.Component;

import ro.mihai.careerhub.dto.response.JobApplicationResponse;
import ro.mihai.careerhub.entity.JobApplication;

@Component
public class JobApplicationMapper {

    public JobApplicationResponse toResponse(
            JobApplication application) {

        return new JobApplicationResponse(
                application.getId(),
                application.getUser().getId(),
                application.getJob().getId(),
                application.getStatus(),
                application.getCreatedate()
        );
    }
}