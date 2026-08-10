package ro.mihai.careerhub.mapper;

import org.springframework.stereotype.Component;

import ro.mihai.careerhub.dto.response.JobResponse;
import ro.mihai.careerhub.entity.Job;

@Component
public class JobMapper {

    public JobResponse toResponse(Job job) {

        return new JobResponse(
                job.getId(),
                job.getTitle(),
                job.getTechnologies(),
                job.getLocation(),
                job.getEmploymentType(),
                job.getCreatedate(),
                job.getCompany().getId()
        );
    }
}