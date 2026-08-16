package ro.mihai.careerhub.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateJobApplicationRequest {

    @NotNull
    private Long jobId;

    public CreateJobApplicationRequest() {
    }

    public CreateJobApplicationRequest(Long jobId) {
        this.jobId = jobId;
    }
}